package com.clean.spaceplus.cleansdk.base.utils.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.clean.spaceplus.cleansdk.util.Env;
import com.hawkclean.mig.commonframework.util.PublishVersionManager;

import java.io.Serializable;
import java.util.ArrayList;

import space.network.util.RuntimeCheck;

//
// 监控管理器
//
//@INTRODUCE
// 整个事件处理过程由三部分组成：事件发送者、监控管理器、事件接收者。
// 监控管理器主要实现了事件的触发与事件的纷发。
//
//@USAGE
// 一.事件发送者
// 1.创建监控器类型（详见：MonitorManager.TYPE_NETWORK_CONNECTIVITY）
// 2.触发事件代码片断
// MonitorManager monitorManager = MonitorManager.getAppContext();
// monitorManager.triggerMonitor(type, param1, param2);
//
// 二.事件接收者
// 1.继承接口MonitorManager.Monitor
// 2.添加监控器
// MonitorManager monitorManager = MonitorManager.getAppContext();
// monitorManager.addMonitor(type, iMonitor, priority); 
//
//@REMARK
// 1.当事件接收者处理事件时，仅允许删除当前事件接收者或别的类型的事件接收者，
//不允许其他任何修改监控器的操作
//
public class MonitorManager {
	//
	// 监控器接口
	//
	public interface Monitor {
		public int monitorNotify(int type, Object param1, Object param2);
	}

	//
	// 优先级
	//
	// @NOTE
	// 数值越小，优先级越高
	//
	public static final int PRIORITY_HIGHEST = 0x00000000;
	public static final int PRIORITY_ABOVE_NORMAL = 0x3FFFFFFF;
	public static final int PRIORITY_NORMAL = 0x4FFFFFFF;
	public static final int PRIORITY_BELOW_NORMAL = 0x5FFFFFFF;
	public static final int PRIORITY_LOWEST = 0x7FFFFFFF;

	//
	// 监控器类型
	//
	public static final int TYPE_UNKNOWN = -1;
	public static int TYPE_SENTRY = 0;
	//
	// 网络连接（屏幕打开与关闭）
	//
	// @NOTE
	// param1 = Context
	// param2 = Intent
	//
	public static final int TYPE_NETWORK_CONNECTIVITY = TYPE_SENTRY++;
	public static final int TYPE_SCREEN_ON = TYPE_SENTRY++;
	public static final int TYPE_SCREEN_OFF = TYPE_SENTRY++;
	public static final int TYPE_PACKAGE_ADD = TYPE_SENTRY++;
	public static final int TYPE_PACKAGE_REMOVE = TYPE_SENTRY++;
	//
	// 系统更新
	//
	// @NOTE
	// param1 = 升级任务类型，详见：UpdateTask
	// param2 = 升级任务对象，详见：UpdateTask
	//
	public static final int TYPE_UPDATE = TYPE_SENTRY++;
	
	// 监听请求root
	public static final int TYPE_ENTER_ROOT = TYPE_SENTRY++;
	
	// 监听所有界面消失
	public static final int TYPE_ACTIVITY_FINISH_ALL = TYPE_SENTRY++;
	
	//监听云端数据刷新
	public static final int TYPE_CLOUD_CONFIG_REFRESH = TYPE_SENTRY++;
	
	//获取到云端各种数据的最新版本号, 这样，各业务数据就可以在只有更新的时候请求了
	public static final int TYPE_CLOUD_DATA_VERSION_INDEX	= TYPE_SENTRY++;
	
	//服务器进程通知 ui 进程
	public static final int TYPE_SERVICE_PROCESS_NOTIFY_UI	= TYPE_SENTRY++;
	
	// 建议清理项剩余大小
	public static final int TYPE_STD_JUNK_SIZE = TYPE_SENTRY++;
	
	// 监听安全模块数据变化
	public static final int TYPE_SECURITY_DATA_NOTIFY = TYPE_SENTRY++;
	
	// 监听内存异常发生
	public static final int TYPE_MEMORY_EXCEPTION_NOTIFY = TYPE_SENTRY++;
	
	//监听CPU异常发生
//	public static final int TYPE_CPU_EXCEPTION_NOTIFY = TYPE_SENTRY++;
	
	// monitor the down load zip success, 通知service 进程中的监听
	public static final int TYPE_DOWN_ZIP_NOTIFY_SERVICE = TYPE_SENTRY++;
	
	// monitor the down load jar success, 通知service 进程
	public static final int TYPE_DOWN_JAR_NOTIFY_SERVICE = TYPE_SENTRY++;
		
	// GCM & Xiaomi message is coming
	public static final int TYPE_3RD_NOTIFICATION_COMING = TYPE_SENTRY++;
	
	//监听充电和拔电状态
	public static final int TYPE_POWER_CONNECT_NOTIFY = TYPE_SENTRY++;
	public static final int TYPE_POWER_DISCONNECT_NOTIFY = TYPE_SENTRY++;

	//监听电池充电保护和耗电过快状态
	public static final int TYPE_BATTERY_DOCTOR_NOTIFY = TYPE_SENTRY++;
	
	
	/**
	 * 游戏盒子要求悬浮窗数据展现请求
	 * */
	public static final int TYPE_GAMEBOX_FLOAT_NOTIFY = TYPE_SENTRY++;

	// 服务进程悬浮窗新闻数据推送通知
    public static final int TYPE_FLOAT_NEWS_NOTIFY = TYPE_SENTRY++;
    
    // 监听悬浮窗浏览器通知
    public static final int TYPE_FLOAT_BROWSER_NOTIFY = TYPE_SENTRY++;
    
	//
	// 返回值
	//
	public static final int RETURN_UNKNOWN = 0;
	public static final int RETURN_CONTINUE = 1;
	public static final int RETURN_BREAK = 2;
	
	private Context mContext = null;
	private boolean mbIsInitialize = false;
	
	private static final String ACTION_IPC_BROADCAST	= "action.com.cleanmaster.ipc.broadcast";
	
	private BroadcastReceiver monitorManagerReceiver = new  BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.compareTo(Intent.ACTION_PACKAGE_ADDED) == 0)
			{
				triggerMonitor(MonitorManager.TYPE_PACKAGE_ADD, context, intent);
			} 
			else if (action.compareTo(Intent.ACTION_PACKAGE_REMOVED) == 0)
			{
				triggerMonitor(MonitorManager.TYPE_PACKAGE_REMOVE, context, intent);
			}
			else if(action.compareTo(Intent.ACTION_SCREEN_ON) == 0)
			{
				triggerMonitor(MonitorManager.TYPE_SCREEN_ON, context, intent);
			}
			else if(action.compareTo(Intent.ACTION_SCREEN_OFF) == 0)
			{
				triggerMonitor(MonitorManager.TYPE_SCREEN_OFF, context, intent);
			}
			else if(action.compareTo(ACTION_IPC_BROADCAST) == 0)
			{
				dispatchIpcBroadcast(intent);
			}
			else if(action.compareTo(Intent.ACTION_POWER_CONNECTED) == 0)
			{
				triggerMonitor(MonitorManager.TYPE_POWER_CONNECT_NOTIFY, context, intent);
			}
			else if(action.compareTo(Intent.ACTION_POWER_DISCONNECTED) == 0)
			{
				triggerMonitor(MonitorManager.TYPE_POWER_DISCONNECT_NOTIFY, context, intent);
			}
		}
	};
	
	// 扩充以处理更多的通信
	private void dispatchIpcBroadcast(Intent intent){
		if (!RuntimeCheck.IsServiceProcess()){
            Serializable ipcData = intent.getSerializableExtra(EXTRA_IPC_BROADCAST);
            if (ipcData != null) {
                if ( ipcData instanceof MonitorIpcArgs) {
                    MonitorIpcArgs	args = (MonitorIpcArgs) ipcData;
                    triggerMonitor(args.type, args.param1, args.param2);
                }
            }
		}
	}
	
	private static final String EXTRA_IPC_BROADCAST = "extra_ipc_broadcast";
	
	private static class MonitorIpcArgs implements Serializable {
		/**
		 *  好强大的东东
		 */
		private static final long serialVersionUID = 4881204553939213467L;
		public int type = 0;
		public Serializable param1 = null;
		public Serializable param2 = null;
		
		public MonitorIpcArgs(){
		}
		public MonitorIpcArgs(int type, Object param1, Object param2){
			this.type 		= type;
			
			if ( param1 != null )
			{
				if ( param1 instanceof Serializable){
					this.param1 = (Serializable) param1;
				}
				else
				{
					if (PublishVersionManager.isTest()) {
						throw new RuntimeException("not support monitor object: " + param1);
					}
				}
			}
			if ( param2 != null )
			{
				if ( param2 instanceof Serializable){
					this.param2 = (Serializable) param2;
				}
				else
				{
					if (PublishVersionManager.isTest()) {
						throw new RuntimeException("not support monitor object: " + param2);
					}
				}
			}
		}
	}

	// 需要跨进程通知的附加的两个参数需要继承于Serializable，不序列化无法通信
	private void initializeIpcBroadcast(){
		this.addMonitor(TYPE_ENTER_ROOT, 				m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_SERVICE_PROCESS_NOTIFY_UI, m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_STD_JUNK_SIZE, 			m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_SECURITY_DATA_NOTIFY, 		m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_MEMORY_EXCEPTION_NOTIFY, 	m_IpcTriggerMonitor, PRIORITY_NORMAL);
//		this.addMonitor(TYPE_CPU_EXCEPTION_NOTIFY, 		m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_BATTERY_DOCTOR_NOTIFY,     m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_GAMEBOX_FLOAT_NOTIFY,      m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_FLOAT_BROWSER_NOTIFY,      m_IpcTriggerMonitor, PRIORITY_NORMAL);
		this.addMonitor(TYPE_FLOAT_NEWS_NOTIFY,         m_IpcTriggerMonitor, PRIORITY_NORMAL);
	}

	private Monitor m_IpcTriggerMonitor = new Monitor() {
		
		@Override
		public int monitorNotify(int type, Object param1, Object param2) {
			Intent intent	= new Intent();
			MonitorIpcArgs		args2	= new MonitorIpcArgs(type, param1, param2);
			intent.setPackage(Env.getPkgName());
			intent.putExtra(EXTRA_IPC_BROADCAST, args2);
			intent.setAction(ACTION_IPC_BROADCAST);
			mContext.sendBroadcast(intent);
			return 0;
		}
	};

	// 获取监控器管理器
	public static synchronized MonitorManager getInstance() {
		if (monitorManager == null) {
			monitorManager = new MonitorManager();
		}
		return monitorManager;
	}
	
	public void initialize(Context context) {
		if (mbIsInitialize) {
			return;
		}
		
		synchronized (this) {
			if (mbIsInitialize) {
				return;
			}
			
			mContext = context;
			
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			mContext.registerReceiver(monitorManagerReceiver, filter);
			
			// 进程间回调，主要用于服务进程通知其他进程有事件产生
			IntentFilter ipcfilter = new IntentFilter();
			ipcfilter.addAction(ACTION_IPC_BROADCAST);
			mContext.registerReceiver(monitorManagerReceiver, ipcfilter);

			IntentFilter packageFilter = new IntentFilter();
			packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
			packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			packageFilter.addDataScheme("package");
			mContext.registerReceiver(monitorManagerReceiver, packageFilter);
			
			IntentFilter powerConnectFilter = new IntentFilter();
			powerConnectFilter.addAction(Intent.ACTION_POWER_CONNECTED);
			powerConnectFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
			mContext.registerReceiver(monitorManagerReceiver, powerConnectFilter);
			
			mbIsInitialize = true;
			
			if (RuntimeCheck.IsServiceProcess()){
				initializeIpcBroadcast();
			}
		}
	}
	
	public void uninitialize() {
		synchronized (this) {
			if (mbIsInitialize) {
				mContext.unregisterReceiver(monitorManagerReceiver);
				mbIsInitialize = false;
			}
		}
	}

	// 增加监控器
	public boolean addMonitor(int type, Monitor iMonitor, int priority) {
		boolean retVal = false;
		if (type > TYPE_UNKNOWN && type < TYPE_SENTRY) {
			ArrayList<MonitorWrap> iMonitorWrapArray = iMonitorWrapMap.get(type);
			synchronized (iMonitorWrapArray) {
				retVal = addMonitor(iMonitorWrapArray, iMonitor, priority);
			}
		}
		return retVal;
	}

	// 获取某类型监控器数量
//	public int getMonitorCount(int type) {
//		int count = 0;
//		if (type > TYPE_UNKNOWN && type < TYPE_SENTRY) {
//			ArrayList<MonitorWrap> iMonitorWrapArray = iMonitorWrapMap.get(type);
//			synchronized (iMonitorWrapArray) {
//				count = iMonitorWrapArray.size();
//			}
//		}
//		return count;
//	}

	//
	// 移除整个监控器
	//
	// @REMARK
	// 在监控器通知回调中，不允许调用此函数
	//
	public boolean removeMonitor(int type) {
		boolean retVal = false;
		if (type > TYPE_UNKNOWN && type < TYPE_SENTRY) {
			ArrayList<MonitorWrap> iMonitorWrapArray = iMonitorWrapMap.get(type);
			synchronized (iMonitorWrapArray) {
				retVal = true;
				iMonitorWrapArray.clear();
			}
		}
		return retVal;
	}

	//
	// 移除特定监控器
	//
	public boolean removeMonitor(int type, Monitor iMonitor) {
		boolean retVal = false;
		if (type > TYPE_UNKNOWN && type < TYPE_SENTRY) {
			ArrayList<MonitorWrap> iMonitorWrapArray = iMonitorWrapMap.get(type);
			synchronized (iMonitorWrapArray) {
				retVal = removeMonitor(iMonitorWrapArray, iMonitor);
			}
		}
		return retVal;
	}

	// 触发监控器
	public int triggerMonitor(int type, Object param1, Object param2) {
		int retVal = RETURN_UNKNOWN;
		if (type > TYPE_UNKNOWN && type < TYPE_SENTRY) {
			ArrayList<MonitorWrap> iMonitorWrapArray = iMonitorWrapMap.get(type);
			synchronized (iMonitorWrapArray) {
				if (!iMonitorWrapArray.isEmpty()) {
					//
					// @NOTE
					// 反向遍历，以防止监控器删除自己导致问题
					//
					for (int i = iMonitorWrapArray.size() - 1; retVal != RETURN_BREAK
							&& i >= 0; --i) {
						MonitorWrap iMonitorWrap = iMonitorWrapArray.get(i);
						retVal = iMonitorWrap.iMonitor.monitorNotify(type,
								param1, param2);
					}
				}
			}
		}
		return retVal;
	}

	// 监控器包装类
	static private class MonitorWrap {
		public MonitorWrap(Monitor iMonitor, int priority) {
			this.priority = priority;
			this.iMonitor = iMonitor;
		}

		public int priority;
		public Monitor iMonitor;
	}

	private static boolean addMonitor(
			ArrayList<MonitorWrap> iMonitorWrapArray, Monitor iMonitor,
			int priority) {
		int location = iMonitorWrapArray.size();
		for (int i = location - 1; i >= 0; --i) {
			MonitorWrap iMonitorWrap = iMonitorWrapArray.get(i);
			if (iMonitorWrap.iMonitor == iMonitor) {
				return false;
			}

			//
			// @NOTE
			// 内部是以反向顺序存储
			//
			if (priority >= iMonitorWrap.priority) {
				--location;
			}
		}

		iMonitorWrapArray.add(location, new MonitorWrap(iMonitor, priority));
		return true;
	}

	private static boolean removeMonitor(
			ArrayList<MonitorWrap> iMonitorWrapArray, Monitor iMonitor) {
		int size = iMonitorWrapArray.size();
		for (int i = 0; i < size; ++i) {
			if (iMonitorWrapArray.get(i).iMonitor == iMonitor) {
				iMonitorWrapArray.remove(i);
				return true;
			}
		}
		return false;
	}

	private MonitorManager() {
		iMonitorWrapMap = new ArrayList<ArrayList<MonitorWrap>>(TYPE_SENTRY);
		for (int i = 0; i < TYPE_SENTRY; ++i) {
			iMonitorWrapMap.add(new ArrayList<MonitorWrap>());
		}
	}

	private ArrayList<ArrayList<MonitorWrap>> iMonitorWrapMap;
	private static MonitorManager monitorManager;
}
