/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dt817
 */
public class Node implements Comparable<Node>, Serializable {

    public String key;    // sorted by key
    public long val;      // associated data
    public Node parent;
    public boolean marked = false;

    public int x;
    public int y;

    public List<Edge> edges;
    public double cost = Double.MAX_VALUE;

    public Node(String key, int val) {
        this.key = key;
        this.val = val;
        edges = new ArrayList();
    }

    @Override
    public int compareTo(Node other) {
        if (this.cost == other.cost) {
            return 0;
        } else {
            return this.cost > other.cost ? 1 : -1;
        }
    }
}
