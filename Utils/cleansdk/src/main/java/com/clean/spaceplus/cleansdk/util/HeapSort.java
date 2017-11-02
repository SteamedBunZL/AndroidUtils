package com.clean.spaceplus.cleansdk.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author liangni
 * @Description:
 * @date 2016/4/29 20:43
 * @copyright TCL-MIG
 */
public class HeapSort {
//    /**
//     * 堆排序算法，取出最大的topNum个元素，放于排序象集合空间的最后。若待排序元
//     * 素个数小于等于topNum，则将所有待排序元素排序。
//     * @param list    待排序对象集合，元素对象必须继承自Comparable<T>
//     * @param topNum  要取出最大元素的个数
//     * @return 成功返回true
//     */
//    public static <T extends Comparable<? super T>> boolean sort(
//            List<T> list,
//            int topNum) {
//        if (null == list || list.isEmpty() || topNum <= 0) {
//            return false;
//        }
//
//        switch (list.size()) {
//            case 1:
//                return true;
//
//            case 2:
//                Collections.sort(list);
//                return true;
//
//            default:
//                break;
//        }
//
//        class CandidateArray implements ICandidateArray {
//
//            public CandidateArray(List<T> list) {
//                mList = list;
//                mSize = list.size();
//            }
//
//            @Override
//            public int size() {
//                return mSize;
//            }
//
//            @Override
//            public int shrink() {
//                return --mSize;
//            }
//
//            @Override
//            public int compare(int leftIdx, int rightIdx) {
//                return mList.get(leftIdx).compareTo(mList.get(rightIdx));
//            }
//
//            @Override
//            public void swap(int leftIdx, int rightIdx) {
//                T temp = mList.get(leftIdx);
//                mList.set(leftIdx, mList.get(rightIdx));
//                mList.set(rightIdx, temp);
//            }
//
//            private int mSize = 0;
//            private final List<T> mList;
//        }
//
//        return sort(new CandidateArray(list), topNum);
//    }
//
//    /**
//     * 堆排序算法，取出最大的topNum个元素，放于排序象集合空间的最后。若待排序元
//     * 素个数小于等于topNum，则将所有待排序元素排序。
//     * @param list    待排序对象集合
//     * @param c       比较方法
//     * @param topNum  要取出最大元素的个数
//     * @return 成功返回true
//     */
//    public static <T> boolean sort(
//            List<T> list,
//            Comparator<? super T> c,
//            int topNum) {
//        if (null == list || list.isEmpty() || null == c || topNum <= 0) {
//            return false;
//        }
//
//        switch (list.size()) {
//            case 1:
//                return true;
//
//            case 2:
//                Collections.sort(list, c);
//                return true;
//
//            default:
//                break;
//        }
//
//        class CandidateArray implements ICandidateArray {
//
//            public CandidateArray(List<T> list, Comparator<? super T> c) {
//                mList = list;
//                mSize = list.size();
//                mCmp  = c;
//            }
//
//            @Override
//            public int size() {
//                return mSize;
//            }
//
//            @Override
//            public int shrink() {
//                return --mSize;
//            }
//
//            @Override
//            public int compare(int leftIdx, int rightIdx) {
//                return mCmp.compare(mList.get(leftIdx), mList.get(rightIdx));
//            }
//
//            @Override
//            public void swap(int leftIdx, int rightIdx) {
//                T temp = mList.get(leftIdx);
//                mList.set(leftIdx, mList.get(rightIdx));
//                mList.set(rightIdx, temp);
//            }
//
//            private int mSize = 0;
//            private final List<T> mList;
//            private final Comparator<? super T> mCmp;
//        }
//
//        return sort(new CandidateArray(list, c), topNum);
//    }

    /**
     * 堆排序算法，取出最大的topNum个元素，放于排序象数组的最后。若待排序元素个
     * 数小于等于topNum，则将所有待排序元素排序。
     * @param a       待排序对象数组，元素对象必须继承自Comparable<T>
     * @param topNum  要取出最大元素的个数
     * @return 成功返回true
     */
/*	public static <T extends Comparable<? super T>> boolean sort(
			T[] a,
			int topNum) {
		return sort(a, a.length, topNum);
	}*/

    /**
     * 堆排序算法，在数组a中[0, sortRange)范围内，取出最大的topNum个元素，放于数
     * 组a中[0, sortRange)范围内的最后。若待排序元素个数小于等于topNum，则将所有
     * 待排序元素排序。
     * @param a          待排序对象数组，元素对象必须继承自Comparable<T>
     * @param sortRange  要排序的对象范围[0, sortRange)
     * @param topNum     要取出最大元素的个数
     * @return 成功返回true
     */
    public static <T extends Comparable<? super T>> boolean sort(
            T[] a,
            int sortRange,
            int topNum) {
        if (null == a || sortRange <= 0 || topNum <= 0) {
            return false;
        }

        switch (sortRange) {
            case 1:
                return true;

            case 2:
                Arrays.sort(a, 0, 2);
                return true;

            default:
                break;
        }

        class CandidateArray implements ICandidateArray {

            public CandidateArray(T[] a, int sortRange) {
                mArray = a;
                mSize  = sortRange;
            }

            @Override
            public int size() {
                return mSize;
            }

            @Override
            public int shrink() {
                return --mSize;
            }

            @Override
            public int compare(int leftIdx, int rightIdx) {
                return mArray[leftIdx].compareTo(mArray[rightIdx]);
            }

            @Override
            public void swap(int leftIdx, int rightIdx) {
                T temp = mArray[leftIdx];
                mArray[leftIdx] = mArray[rightIdx];
                mArray[rightIdx] = temp;
            }

            private int mSize = 0;
            private final T[] mArray;
        }

        return sort(new CandidateArray(a, sortRange), topNum);
    }

    /**
     * 堆排序算法，取出最大的topNum个元素，放于排序象数组的最后。若待排序元素个
     * 数小于等于topNum，则将所有待排序元素排序。
     * @param a       待排序对象数组
     * @param topNum  要取出最大元素的个数
     * @param c       比较方法
     * @return 成功返回true
     */
/*	public static <T> boolean sort(
			T[] a,
			int topNum,
            Comparator<? super T> c) {
		return sort(a, a.length, topNum, c);
	}*/

    /**
     * 堆排序算法，在数组a中[0, sortRange)范围内，取出最大的topNum个元素，放于数
     * 组a中[0, sortRange)范围内的最后。若待排序元素个数小于等于topNum，则将所有
     * 待排序元素排序。
     * @return
     */
/*	public static <T> boolean sort(
			T[] a,
			int sortRange,
			int topNum,
            Comparator<? super T> c) {
		if (null == a || sortRange <= 0 || null == c || topNum <= 0) {
			return false;
		}

		switch (sortRange) {
		case 1:
			return true;

		case 2:
			Arrays.sort(a, 0, 2, c);
			return true;

		default:
			break;
		}

		class CandidateArray implements ICandidateArray {

			public CandidateArray(T[] a, int sortRange, Comparator<? super T> c) {
				mArray = a;
				mSize  = sortRange;
				mCmp   = c;
			}

			@Override
			public int size() {
				return mSize;
			}

			@Override
			public int shrink() {
				return --mSize;
			}

			@Override
			public int compare(int leftIdx, int rightIdx) {
				return mCmp.compare(mArray[leftIdx], mArray[rightIdx]);
			}

			@Override
			public void swap(int leftIdx, int rightIdx) {
				T temp = mArray[leftIdx];
				mArray[leftIdx] = mArray[rightIdx];
				mArray[rightIdx] = temp;
			}

			private int mSize = 0;
			private final T[] mArray;
			private final Comparator<? super T> mCmp;
		}

		return sort(new CandidateArray(a, sortRange, c), topNum);
	}*/



    private static final int getLeftChild(int idx) {
        return ((idx << 1) + 1);
    }

    private static final int getRightChild(int idx) {
        return ((idx << 1) + 2);
    }

    private static final int getParent(int idx) {
        return ((idx - 1) >> 1);
    }

    private static boolean maxHeapify(ICandidateArray a, int topIdx) {
        int size  = a.size();
        int left  = 0;
        int right = 0;
        int max   = 0;

        do {
            max   = topIdx;
            left  = getLeftChild(topIdx);
            right = getRightChild(topIdx);

            if (left < size && a.compare(max, left) < 0) {
                max = left;
            }

            if (right < size && a.compare(max, right) < 0) {
                max = right;
            }

            if (max == topIdx) {
                break;
            }

            a.swap(max, topIdx);
            topIdx = max;
        } while (true);

        return true;
    }

    private static boolean buildMaxHeap(ICandidateArray a) {
        int i = getParent(a.size() - 1);

        while (i >= 0) {
            if (!maxHeapify(a, i)) {
                return false;
            }

            --i;
        }

        return true;
    }

    private static boolean heapSort(ICandidateArray a, int topNum) {
        boolean rc = false;
        do {
            a.swap(0, a.size() - 1);
            if (--topNum <= 0) {
                rc = true;
                break;
            }

            if (a.shrink() <= 0) {
                rc = true;
                break;
            }

            rc = maxHeapify(a, 0);
        } while (rc);

        return rc;
    }

    /**
     * 堆排序算法，取出最大的topNum个元素，放于排序象数组的最后。若待排序元素个
     * 数小于等于topNum，则将所有待排序元素排序。
     * @param a       待排序对象抽象数组，至少要有3个元素。
     * @param topNum  要取出最大元素的个数
     * @return 成功返回true
     */
    private static boolean sort(
            ICandidateArray a,
            int topNum) {
        if (null == a || a.size() < 3 || 0 == topNum) {
            return false;
        }

        boolean rc = buildMaxHeap(a);
        if (!rc) {
            return rc;
        }

        return heapSort(a, topNum);
    }

    private interface ICandidateArray {
        public int size();
        public int shrink();
        public int compare(int leftIdx, int rightIdx);
        public void swap(int leftIdx, int rightIdx);
    }

}
