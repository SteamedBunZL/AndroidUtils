/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.leakcanary;

import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import static com.squareup.leakcanary.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Watches references that should become weakly reachable. When the {@link RefWatcher} detects that
 * a reference might not be weakly reachable when it should, it triggers the {@link HeapDumper}.
 *
 * <p>This class is thread-safe: you can call {@link #watch(Object)} from any thread.
 */
public final class RefWatcher {

  public static final RefWatcher DISABLED = new RefWatcher(new Executor() {
    @Override public void execute(Runnable command) {
    }
  }, new DebuggerControl() {
    @Override public boolean isDebuggerAttached() {
      // Skips watching.
      return true;
    }
  }, GcTrigger.DEFAULT, new HeapDumper() {
    @Override public File dumpHeap() {
      return null;
    }
  }, new HeapDump.Listener() {
    @Override public void analyze(HeapDump heapDump) {
    }
  }, new ExcludedRefs.Builder().build());

  private final Executor watchExecutor;//watch 线程池，持行watch及dump文件的操作
  private final DebuggerControl debuggerControl;
  private final GcTrigger gcTrigger;//GC 触发器
  private final HeapDumper heapDumper;//heap dump
  private final Set<String> retainedKeys;//保留key 用于判断activity的泄漏
  private final ReferenceQueue<Object> queue;//Activity弱引用回收的队列
  private final HeapDump.Listener heapdumpListener;
  private final ExcludedRefs excludedRefs;

  public RefWatcher(Executor watchExecutor, DebuggerControl debuggerControl, GcTrigger gcTrigger,
      HeapDumper heapDumper, HeapDump.Listener heapdumpListener, ExcludedRefs excludedRefs) {
    this.watchExecutor = checkNotNull(watchExecutor, "watchExecutor");
    this.debuggerControl = checkNotNull(debuggerControl, "debuggerControl");
    this.gcTrigger = checkNotNull(gcTrigger, "gcTrigger");
    this.heapDumper = checkNotNull(heapDumper, "heapDumper");
    this.heapdumpListener = checkNotNull(heapdumpListener, "heapdumpListener");
    this.excludedRefs = checkNotNull(excludedRefs, "excludedRefs");
    retainedKeys = new CopyOnWriteArraySet<>();//并发容器set 支持并发的修改
    queue = new ReferenceQueue<>();
  }

  /**
   * Identical to {@link #watch(Object, String)} with an empty string reference name.
   *
   * @see #watch(Object, String)
   */
  public void watch(Object watchedReference) {
    watch(watchedReference, "");
  }

  /**
   * Watches the provided references and checks if it can be GCed. This method is non blocking,
   * the check is done on the {@link Executor} this {@link RefWatcher} has been constructed with.
   *
   * @param referenceName An logical identifier for the watched object.
   */
  public void watch(Object watchedReference, String referenceName) {
    checkNotNull(watchedReference, "watchedReference");
    checkNotNull(referenceName, "referenceName");
    if (debuggerControl.isDebuggerAttached()) {
      return;
    }
    final long watchStartNanoTime = System.nanoTime();
    String key = UUID.randomUUID().toString();//给actiivty分配了UUID 做为Key
    retainedKeys.add(key);//遗留key set中加入key
    final KeyedWeakReference reference =
        new KeyedWeakReference(watchedReference, key, referenceName, queue);

    watchExecutor.execute(new Runnable() {
      @Override public void run() {
        ensureGone(reference, watchStartNanoTime);//确认activity是否泄漏
      }
    });
  }

  void ensureGone(KeyedWeakReference reference, long watchStartNanoTime) {
    long gcStartNanoTime = System.nanoTime();

    long watchDurationMs = NANOSECONDS.toMillis(gcStartNanoTime - watchStartNanoTime);
    removeWeaklyReachableReferences();
    if (gone(reference) || debuggerControl.isDebuggerAttached()) {
      return;
    }
    gcTrigger.runGc();//运行GC 这样可以回收掉弱引用
    removeWeaklyReachableReferences();//再次确认，引用队列
    if (!gone(reference)) {//说明发生了泄漏，如果Activity在销毁时，进入了弱引用的回收队列，说明被回收掉了，如果没进入，说明回掉不掉这个Activity引用
      long startDumpHeap = System.nanoTime();
      long gcDurationMs = NANOSECONDS.toMillis(startDumpHeap - gcStartNanoTime);

      File heapDumpFile = heapDumper.dumpHeap();//持行dump 文件操作生成dump file

      if (heapDumpFile == HeapDumper.NO_DUMP) {
        // Could not dump the heap, abort.
        return;
      }
      long heapDumpDurationMs = NANOSECONDS.toMillis(System.nanoTime() - startDumpHeap);//dump 时间

      //执行Analyze
      heapdumpListener.analyze(
          new HeapDump(heapDumpFile, reference.key, reference.name, excludedRefs, watchDurationMs,
              gcDurationMs, heapDumpDurationMs));
    }
  }

  private boolean gone(KeyedWeakReference reference) {
    return !retainedKeys.contains(reference.key);//判断是否遗留set中是否包含key,如果还包含说明发生了泄漏
  }

  private void removeWeaklyReachableReferences() {
    // WeakReferences are enqueued as soon as the object to which they point to becomes weakly
    // reachable. This is before finalization or garbage collection has actually happened.

    //是说对象变得弱引用并且进入队列是在finalization和垃圾回收器回收之前
    KeyedWeakReference ref;
    while ((ref = (KeyedWeakReference) queue.poll()) != null) {
      retainedKeys.remove(ref.key);//如果可回收，把它从遗留的set中去掉
    }
  }
}
