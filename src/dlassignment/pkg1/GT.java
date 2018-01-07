/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;


import dlassignment.pkg1.Tree.Node;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author dt817
 */
public class GT {

    Node root;
    int totalNodes = 0;
    int maxheight = 0;
    final int constant = 5;

    public GT() {
        root = null;
    }

    public int treeHeight(Node t) {
        if (t == null) {
            return -1;
        } else {
            return 1 + max(treeHeight(t.left), treeHeight(t.right));
        }
    }

    public int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public void computeNodePositions() {
        int depth = 1;
        inorder_traversal(root, depth);
    }

    public void inorder_traversal(Node t, int depth) {
        if (t != null) {
            inorder_traversal(t.left, depth + 1);
            t.x = totalNodes++;
            t.y = depth;
            inorder_traversal(t.right, depth + 1);
        }
    }

    public void printLevelOrder(Node o, ArrayList<dlassignment.pkg1.Node> path, ArrayList<String> ids) throws IOException {

        int h = height(o);
        int i;
        this.maxheight = h;
        for (i = 1; i <= h; i++) {
            printGivenLevel(o, i);
            System.out.println();
        }
        this.root = o;
        GraphicTree dt = new GraphicTree(this, path, ids);
        dt.setVisible(true);
    }

    /* Compute the "height" of a tree -- the number of
    nodes along the longest path from the root node
    down to the farthest leaf node.*/
    int height(Node root) {
        if (root == null) {
            return 0;
        } else {
            /* compute  height of each subtree */
            int lheight = height(root.left);
            int rheight = height(root.right);

            /* use the larger one */
            if (lheight > rheight) {
                return (lheight + 1);
            } else {
                return (rheight + 1);
            }
        }
    }

    /* Print nodes at the given level */
    void printGivenLevel(Node r, int level) throws IOException {

        if (r == null) {
            return;
        }
        // root = insert(root, r.val);
        // r.y = maxheight+1 - level;
        // r.x = totalNodes++;
        if (level == 1) {
            System.out.print(r.val + " ");
            totalNodes++;
            r.x = totalNodes;
            r.y = maxheight - level;
        } else if (level > 1) {
            System.out.print(r.val + " ");
            printGivenLevel(r.left, level - 1);
            totalNodes++;
            r.x = totalNodes;
            r.y = maxheight - level;
            printGivenLevel(r.right, level - 1);
           
        }
       
    }


    public Node insert(Node root, long val) {
        if (root == null) {
            root = new Node("", val, 0);
            return root;
        } else {
            if (val == root.val) {
                return root;
            } else if (val < root.val) {
                root.left = insert(root.left, val);
            } else {
                root.right = insert(root.right, val);
            }
            return root;
        }
    }
}
