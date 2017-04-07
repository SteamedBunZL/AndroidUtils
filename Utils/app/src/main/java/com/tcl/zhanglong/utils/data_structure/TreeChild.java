package com.tcl.zhanglong.utils.data_structure;

import java.util.ArrayList;
import java.util.List;

/**
 * 子节点链表法
 *
 * Created by Steve on 17/2/17.
 */

public class TreeChild<E> {

    private static class SonNode{
        //
        private int pos;
        private SonNode next;

        public SonNode(int pos, SonNode next) {
            this.pos = pos;
            this.next = next;
        }
    }

    public static class Node<T>{
        T data;
        //
        SonNode first;

        public Node(T data) {
            this.data = data;
        }

    }

    private final int DEFAULT_TREE_SIZE = 100;
    private int treeSize = 0;
    //
    private Node<E>[] nodes;
    //记录节点数
    private int nodeNums;

    //
    public TreeChild(E data) {
        treeSize = DEFAULT_TREE_SIZE;
       nodes = new Node[treeSize];
        nodes[0] = new Node<>(data);
        nodeNums++;
    }

    //
    public TreeChild(E data,int treeSize) {
        this.treeSize = treeSize;
        nodes = new Node[treeSize];
        nodes[0] = new Node<>(data);
        nodeNums++;
    }

    public void addNode (E data,Node parent){
        for(int i = 0;i<treeSize;i++){
            if (nodes[i]==null){
                nodes[i] = new Node<>(data);
                if (parent.first == null){
                    parent.first = new SonNode(i,null);
                }else{
                    SonNode next = parent.first;
                    while (next.next!=null){
                        next = next.next;
                    }
                    next.next = new SonNode(i,null);
                }
                nodeNums++;
                return;
            }
        }
        throw new RuntimeException("该树已满,无法添加新节点");
    }

    public boolean empty(){
        return nodes[0]==null;
    }

    public Node root(){
        return nodes[0];
    }

    public List<Node> children(Node parent){
        List<Node> list = new ArrayList<>();
        SonNode next = parent.first;
        while (next!=null){
            list.add(nodes[next.pos]);
            next = next.next;
        }
        return list;
    }

    public Node child(Node parent,int index){
        SonNode next = parent.first;
        for (int i = 0;next!=null;i++){
            if (index==i){
                return nodes[next.pos];
            }
            next = next.next;
        }
        return null;
    }

    private int deep(Node node){
        if (node.first == null){
            return 1;
        }else{
            int max = 0;
            SonNode next= node.first;
            while(next!=null){
                int tmp = deep(nodes[next.pos]);
                if (tmp>max){
                    max = tmp;
                }
                next = next.next;
            }
            return max + 1;
        }
    }




}
