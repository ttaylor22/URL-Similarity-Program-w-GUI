/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;

/**
 *
 * @author dt817
 */
public class DijkstrasAlgorithm {

    Tree tree = new Tree();

    public ArrayList<Node> Dijkstras(Node src, Node dest) throws CloneNotSupportedException {
        //height within the btree
        int level = 1;
        src.cost = 0;
        //create the PriorityQueue which will keep track of the vertices by minimum distance
        PriorityQueue<Node> priorityQueue = new PriorityQueue();
        //push the current Node into the queue
        priorityQueue.add(src);
        src.x = 6;
        src.y = 1;
        //continue while the queue is not empty
        while (!priorityQueue.isEmpty()) {
            //current is the next Node in the queue, or the Node with the minimum distance
            src = priorityQueue.remove();
            ++level;
            //if we are currently looking at the dest Node, break out of the loop
            if (src.key.equals(dest.key)) {
                System.out.println("Cluster: " + dest.key + " : " + dest.val + " -Found-  (" + src.cost + ")");
                return getPathTo(src);
            }
            //examine each neighbor of the current Node by looking at each edge that connects them
            int numOfnodes = 0;
            for (Edge edge : src.edges) {
                //end keeps track of the Node opposite of the current Node on the edge
                Node end = edge.dest;
                double distance = edge.weight;
                double totalDistance = src.cost + distance;
                //if the totalDistance is less than the minimum distance of the end Node, replace it
                if (totalDistance < end.cost && end.marked == false) {
                    priorityQueue.remove(end);
                    //Update node's distance
                    end.cost = totalDistance;
                    end.marked = true;
                    end.parent = src;
                    end.x = ++numOfnodes;
                    end.y = level;
                    priorityQueue.add(end);
                }
            }
        }
        System.out.println("Cluster: " + dest.key + " : " + dest.val + " -Unreachable-");
        return null;
    }

    public ArrayList<Node> getPathTo(Node target) {
        ArrayList<Node> path = new ArrayList();
        for (Node node = target; node != null; node = node.parent) {
            if (path.contains(node)) {
                break;
            }
            path.add(node);
        }
        //path.remove(path.size()-1);
        Collections.reverse(path);
        return path;
    }

}
