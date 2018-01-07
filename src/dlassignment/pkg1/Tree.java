/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 *
 * @author dt817
 */
public class Tree implements Serializable {

    public Node root;  
   
    HashMap<String, ArrayList<EdgeSerialization>> tracker = new HashMap();
    public ArrayList<Node> list = new ArrayList();
    public DLAssignment1 dl = null;

    public static class Node implements Comparable<Node>, Serializable {

        public String key;           // sorted by key
        public long val;         // associated data
        public Node left, right;  // left and right subtrees
      
        public int size;          // number of nodes in subtree

        public int x;
        public int y;
        public int depth = 0;

        public List<Edge> edges;
        public double id = Double.MAX_VALUE;

        public Node(String key, long val, int size) {
            this.key = key;
            this.size = size;
            this.val = val;
            edges = new ArrayList();
        }

        public Node() {
            key = "";
            size = 0;
            edges = new ArrayList();
        }

        public Node(Node newNode) {
            List<Edge> newEdgeList = new ArrayList();
            for (Edge e : newNode.edges) {
                newEdgeList.add(new Edge(e.src, e.dest, e.weight));
            }
            this.key = newNode.key;
            this.val = newNode.val;

            this.size = newNode.size;
            this.x = newNode.x;
            this.y = newNode.y;
            this.edges = newEdgeList;
            
            this.left = newNode.left;
            this.right = newNode.right;
        }

        @Override
        public int compareTo(Node other) {
            if (this.val == other.val) {
                return 0;
            } else {
                return this.val > other.val ? 1 : -1;
            }
        }
    }

    public Node getRoot() {
        return root;
    }

    /**
     * Initializes an empty symbol table.
     *
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    

    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty; {@code false}
     * otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return size(root);
    }

    // return number of key-value pairs in BST rooted at x
    private int size(Node x) {
        if (x == null) {
            return 0;
        } else {
            return x.size;
        }
    }

    /**
     * Does this symbol table contain the given key?
     *
     * @param key the key
     * @return {@code true} if this symbol table contains {@code key} and
     * {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        if (key == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }
        return get(key) != null;
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key
     * @return the value associated with the given key if the key is in the
     * symbol table and {@code null} if the key is not in the symbol table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Node get(String key) {
        return get(root, key);
    }

    private Node get(Node x, String key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get() with a null key");
        }
        if (x == null) {
            return null;
        }
        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            return get(x.left, key);
        } else if (cmp > 0) {
            return get(x.right, key);
        } else {
            return x;
        }
    }

    /**
     * Inserts the specified key-value pair into the symbol table, overwriting
     * the old value with the new value if the symbol table already contains the
     * specified key. Deletes the specified key (and its associated value) from
     * this symbol table if the specified value is {@code null}.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void put(String key, long val) {
        if (key == null) {
            throw new IllegalArgumentException("calls put() with a null key");
        }
        if (val == 0) {
            delete(key);
            return;
        }
        root = put(root, key, val);
    }

    private Node put(Node x, String key, long val) {
        if (x == null) {
            return new Node(key, val, 1);
        }
        int cmp = key.compareTo(x.key);

        if (cmp < 0) {
            x.left = put(x.left, key, val);
           
        } else if (cmp > 0) {
            x.right = put(x.right, key, val);
            
        } else {
            x.val = val;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }
    
    void storeBSTNodes(Node root, Vector<Node> nodes) 
    {
        // Base case
        if (root == null)
            return;
 
        // Store nodes in Inorder (which is sorted
        // order for BST)
        storeBSTNodes(root.left, nodes);
        nodes.add(root);
        storeBSTNodes(root.right, nodes);
    }
 
    /* Recursive function to construct binary tree */
    Node buildTreeUtil(Vector<Node> nodes, int start, int end) {
        // base case
        if (start > end)
            return null;
 
        /* Get the middle element and make it root */
        int mid = (start + end) / 2;
        Node node = nodes.get(mid);
 
        /* Using index in Inorder traversal, construct
           left and right subtress */
        node.left = buildTreeUtil(nodes, start, mid - 1);
        node.right = buildTreeUtil(nodes, mid + 1, end);
 
        return node;
    }
 
    // This functions converts an unbalanced BST to
    // a balanced BST
    public Node buildTree(Node root) 
    {
        // Store nodes of given BST in sorted order
        Vector<Node> nodes = new Vector<Node>();
        storeBSTNodes(root, nodes);
 
        // Constucts BST from nodes[]
        int n = nodes.size();
        return buildTreeUtil(nodes, 0, n - 1);
    }

    

    /**
     * Removes the smallest key and associated value from the symbol table.
     *
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Symbol table underflow");
        }
        root = deleteMin(root);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) {
            return x.right;
        }
        x.left = deleteMin(x.left);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    /**
     * Removes the largest key and associated value from the symbol table.
     *
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Symbol table underflow");
        }
        root = deleteMax(root);
    }

    private Node deleteMax(Node x) {
        if (x.right == null) {
            return x.left;
        }
        x.right = deleteMax(x.right);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    /**
     * Removes the specified key and its associated value from this symbol table
     * (if the key is in this symbol table).
     *
     * @param key the key
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void delete(String key) {
        if (key == null) {
            throw new IllegalArgumentException("calls delete() with a null key");
        }
        root = delete(root, key);
    }

    private Node delete(Node x, String key) {
        if (x == null) {
            return null;
        }

        int cmp = key.compareTo(x.key);
        if (cmp < 0) {
            x.left = delete(x.left, key);
        } else if (cmp > 0) {
            x.right = delete(x.right, key);
        } else {
            if (x.right == null) {
                return x.left;
            }
            if (x.left == null) {
                return x.right;
            }
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    /**
     * Returns the smallest key in the symbol table.
     *
     * @return the smallest key in the symbol table
     * @throws NoSuchElementException if the symbol table is empty
     */
    public String min() {
        if (isEmpty()) {
            throw new NoSuchElementException("calls min() with empty symbol table");
        }
        return min(root).key;
    }

    private Node min(Node x) {
        if (x.left == null) {
            return x;
        } else {
            return min(x.left);
        }
    }

  

    public void levelOrderQueueForGraph(Node root, ArrayList<Node> ids, ArrayList<Node> allGivenNodes) throws IOException {
        PriorityQueue<Node> q = new PriorityQueue<>();
        GT t = new GT();
        if (root == null) {
            return;
        }
        q.add(root);
        while (!q.isEmpty()) {

            Node n = q.poll();

            t.root = t.insert(t.root, n.val);
            if (!allGivenNodes.isEmpty()) {
                if (allGivenNodes.contains(n)) {
                    allGivenNodes.remove(n);
                }
            } else {
                break;
            }
            if (n.left != null) {
                q.add(n.left);
            }
            if (n.right != null) {
                q.add(n.right);
            }
        }

        t.computeNodePositions();
        t.maxheight = t.treeHeight(t.root);
       // GraphicTree dt = new GraphicTree(t, ids);
       // dt.setVisible(true);
    }

//    public void printLevelOrder(Node o, String shortest, ArrayList<String> ids) throws IOException {
//        int h = height(o);
//        int i;
//        for (i = 1; i <= h; i++) {
//            printGivenLevel(o, i);
//        }
//        gt.maxheight = h;
//        GraphicTree dt = new GraphicTree(gt, shortest, ids);
//        dt.setVisible(true);
//    }
//
//    /* Compute the "height" of a tree -- the number of
//    nodes along the longest path from the root node
//    down to the farthest leaf node.*/
//    int height(Node root) {
//        if (root == null) {
//            return 0;
//        } else {
//            /* compute  height of each subtree */
//            int lheight = height(root.left);
//            int rheight = height(root.right);
//
//            /* use the larger one */
//            if (lheight > rheight) {
//                return (lheight + 1);
//            } else {
//                return (rheight + 1);
//            }
//        }
//    }
//
//    /* Print nodes at the given level */
//    void printGivenLevel(Node root, int level) throws IOException {
//
//        if (root == null) {
//            return;
//        }
//        gt.root = gt.insert(gt.root, root.val);
//        root.y = level;
//        root.x = gt.totalNodes++;
//        if (level == 1) {
//            System.out.print(root.val + " ");
//        } else if (level > 1) {
//            System.out.print(root.val + " ");
//            printGivenLevel(root.left, level - 1);
//            printGivenLevel(root.right, level - 1);
//        }
//    }

    public void displayBFS(Node root) {

        PriorityQueue<Node> q = new PriorityQueue();
        q.add(root);
        int numInCurrentLevel = 1;
        int numInNextLevel = 0;
        int indexInCurrentLevel = 0;

        while (!q.isEmpty()) {
            Node node = q.poll();
            System.out.print(node.val + " ");
            indexInCurrentLevel++;
            node.x = indexInCurrentLevel;
            node.y = numInNextLevel;

            if (node.left != null) {
                q.add(node.left);
                numInNextLevel++;
            }
            if (node.right != null) {
                q.add(node.right);
                numInNextLevel++;
            }

            //finish traversal in current level
            if (indexInCurrentLevel == numInCurrentLevel) {
                System.out.println();
                numInCurrentLevel = numInNextLevel;
                numInNextLevel = 0;
                indexInCurrentLevel = 0;
            }
        }
    }

    /*Given a binary tree, print out all of its root-to-leaf
      paths, one per line. Uses a recursive helper to do 
      the work.*/
    void printPaths(Node node) {
        double path[] = new double[1000];
        printPathsRecur(node, path, 0);
    }

    /* Recursive helper function -- given a node, and an array
       containing the path from the root node up to but not 
       including this node, print out all the root-leaf paths.*/
    void printPathsRecur(Node node, double path[], int pathLen) {
        if (node == null) {
            return;
        }

        /* append this node to the path array */
        path[pathLen] = node.val;
        pathLen++;

        /* it's a leaf, so print the path that led to here  */
        if (node.left == null && node.right == null) {
            printArray(path, pathLen);
        } else {
            /* otherwise try both subtrees */
            printPathsRecur(node.left, path, pathLen);
            printPathsRecur(node.right, path, pathLen);
        }
    }

    /* Utility function that prints out an array on a line. */
    void printArray(double ints[], int len) {
        int i;
        for (i = 0; i < len; i++) {
            System.out.print(ints[i] + " ");
        }
        System.out.println("");
    }
}
