package com.tcl.zhanglong.utils.data_structure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * 二叉树的三叉链表存储
 * Created by Steve on 17/2/20.
 */

public class TreeLinkBinThree<E> {

    public static class TreeNode{
        Object data;
        TreeNode left;
        TreeNode right;
        TreeNode parent;

        public TreeNode() {
        }

        public TreeNode(Object data) {
            this.data = data;
        }

        public TreeNode(Object data, TreeNode left, TreeNode right, TreeNode parent) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.parent = parent;
        }
    }

    private TreeNode root;

    public TreeLinkBinThree() {
        root = new TreeNode();
    }

    public TreeLinkBinThree(E data) {
        this.root = new TreeNode(data);
    }

    public TreeNode addNode(TreeNode parent,E data,boolean isLeft){
        if (parent==null)
            throw new RuntimeException("");

        if (isLeft&&parent.left!=null)
            throw new RuntimeException("");

        if (!isLeft&&parent.right!=null){
            throw new RuntimeException("");
        }

        TreeNode newNode = new TreeNode(data);
        if (isLeft){
            parent.left = newNode;
        }else{
            parent.right = newNode;
        }
        parent.parent = parent;
        return newNode;
    }

    public boolean empty(){
        return root.data == null;
    }

    public TreeNode root(){
        if (empty())
            throw new RuntimeException("");

        return root;
    }

    public E parent(TreeNode node){
        if (node==null)
            throw new RuntimeException("");

        return (E) node.parent;
    }

    public E leftChild(TreeNode parent){
        if (parent == null){
            throw new RuntimeException("");
        }
        return parent.left == null?null: (E) parent.left;
    }

    public E rightChild(TreeNode parent){
        if (parent==null){
            throw new RuntimeException("");
        }
        return parent.right == null?null: (E) parent.right;
    }

    private int deep(TreeNode node){
        if (node == null){
            return 0;
        }

        if (node.left == null && node.right == null){
            return 1;
        }else{
            int leftDeep = deep(node.left);
            int rightDeep = deep(node.right);
            int max = leftDeep>rightDeep?leftDeep:rightDeep;
            return max + 1;
        }
    }

    //遍历二叉树
    public List<TreeNode> preIterator(){
        return preIterator(root);
    }

    //先序遍历
    private List<TreeNode> preIterator(TreeNode node){
        List<TreeNode> list = new ArrayList<>();

        list.add(node);

        if (node.left!=null)
            list.addAll(preIterator(node.left));

        if (node.right!=null)
            list.addAll(preIterator(node.right));

        return list;
    }

    //中序遍历
    private List<TreeNode> inInterator(TreeNode node){
        List<TreeNode> list = new ArrayList<>();

        if (node.left!=null)
            list.addAll(preIterator(node.left));

        list.add(node);

        if (node.right!=null)
            list.addAll(preIterator(node.right));

        return list;
    }

    //后序遍历
    private List<TreeNode> postIterator(TreeNode node){
        List<TreeNode> list = new ArrayList<>();

        if (node.left!=null)
            list.addAll(postIterator(node.left));

        if (node.right!=null)
            list.addAll(postIterator(node.right));

        list.add(node);

        return list;
    }

    //广度优先遍历
    public List<TreeNode> breathFirst(){
        Queue<TreeNode> queue = new ArrayDeque<>();
        List<TreeNode> list = new ArrayList<>();

        if (root!=null)
            queue.offer(root);

        while(!queue.isEmpty()){
            list.add(queue.peek());
            TreeNode p = queue.poll();
            if (p.left!=null)
                queue.offer(p.left);

            if (p.right!=null)
                queue.offer(p.right);

        }

        return list;
    }


}
