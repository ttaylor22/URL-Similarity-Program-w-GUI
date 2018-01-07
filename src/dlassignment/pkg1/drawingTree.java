/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import dlassignment.pkg1.Tree.Node;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 *
 * @author dt817
 */
public class drawingTree extends JPanel {

    GT t;
    int x;
    int y;
    ArrayList<dlassignment.pkg1.Node> path;
    boolean isTrue = false;
    ArrayList<String> ids = new ArrayList();

    drawingTree(GT t, ArrayList<dlassignment.pkg1.Node> path, ArrayList<String> ids) {
        this.t = t;
        this.ids = ids;
        this.path = path;
        setBackground(Color.white);
        setForeground(Color.black);
    }

    public void paintComponent(Graphics g) {
        g.setColor(getBackground()); //colors the window
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground()); //set color and fonts
        Font MyFont = new Font("SansSerif", Font.PLAIN, 10);
        g.setFont(MyFont);
        x = 10;   //where to start printing on the panel
        y = 20;
        g.drawString("Shortest Path Tree for the input value: " + t.root.val, x, y);
        y = y + 10;

        g.drawString("Number of Spanning Trees: " + ids.size(), x, y + 1);
        MyFont = new Font("SansSerif", Font.BOLD, 15); //bigger font for tree
        g.setFont(MyFont);
        this.drawTree(g, t.root, "black"); // draw the tree
        revalidate(); //update the component panel
    }

    public void drawTree(Graphics g, Node root, String color) {//actually draws the tree

        int dx, dy, dx2, dy2;
        int SCREEN_WIDTH = 800; //screen size for panel
        int SCREEN_HEIGHT = 700;
        int XSCALE, YSCALE;
        XSCALE = SCREEN_WIDTH / t.totalNodes; //scale x by total nodes in tree
        YSCALE = (SCREEN_HEIGHT - y) / (t.maxheight + 1); //scale y by tree height

        if (root != null) { // inorder traversal to draw each node
            dx = root.x * XSCALE; // get x,y coords., and scale them 
            dy = root.y * YSCALE + y;
            long s = root.val; //get the word at this node
            String a = String.valueOf(s);
            g.drawString(a, dx, dy); // draws the word
            if (ids.contains(root.key)) {
                g.setColor(Color.RED);
            }
            for (dlassignment.pkg1.Node e : path) {
                if (e.val == root.val) {
                    g.setColor(Color.GREEN);
                }
            }
            g.drawOval(dx, dy - 25, 45, 30);
            g.setColor(Color.BLACK);
            // this draws the lines from a node to its children, if any
            if (root.left != null) { //draws the line to left child if it exists
                dx2 = root.left.x * XSCALE;
                dy2 = root.left.y * YSCALE + y;
                g.drawLine(dx, dy, dx2, dy2);
            }
            drawTree(g, root.left, color); // do left side of inorder traversal 
            if (root.right != null) { //draws the line to right child if it exists
                dx2 = root.right.x * XSCALE;//get right child x,y scaled position
                dy2 = root.right.y * YSCALE + y;
                g.drawLine(dx, dy, dx2, dy2);
            }
            drawTree(g, root.right, color); //now do right side of inorder traversal 
        }
    }
}
