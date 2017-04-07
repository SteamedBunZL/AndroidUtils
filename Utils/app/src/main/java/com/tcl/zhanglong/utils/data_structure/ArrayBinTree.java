package com.tcl.zhanglong.utils.data_structure;

/**
 * 顺序存储二叉树
 *
 * Created by Steve on 17/2/20.
 */

public class ArrayBinTree<T> {

    private Object[] datas;
    private int DEFAULT_DEEP = 8;

    private int deep;
    private int arraySize;

    public ArrayBinTree() {
        this.deep = DEFAULT_DEEP;
        this.arraySize = (int) (Math.pow(2,deep) - 1);
        datas = new Object[arraySize];
    }

    public ArrayBinTree(int deep) {
        this.deep = deep;
        this.arraySize = (int) (Math.pow(2,deep) - 1);
        datas = new Object[arraySize];
    }

    public ArrayBinTree(int deep,T data) {
        this.deep = deep;
        this.arraySize = (int) (Math.pow(2,deep) -1);
        datas = new Object[arraySize];
        datas[0] = data;
    }

    public void add(int index,T data,boolean left){
        if (datas[index] == null){
            throw new RuntimeException("");
        }

        if (2*index + 1>=arraySize){
            throw new RuntimeException("");
        }

        if (left){
            datas[2*index + 1] = data;
        }else{
            datas[2*index + 2] = data;
        }
    }

    public boolean empty(){
        return datas[0]==null;
    }

    public T root(){
        return (T) datas[0];
    }

    public T parent(int index){
        return (T) datas[(index-1)/2];
    }

    public T left(int index){
        if ((2*index+1)>=arraySize){
            return null;
        }else{
            return (T) datas[2*index+1];
        }
    }

    public T right(int index){
        if ((2*index+2>=arraySize)){
            return null;
        }else{
            return (T) datas[2*index+2];
        }
    }

    public int deep(int index){
        return deep;
    }

}
