/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

/**
 *
 * @author dt817
 */
public class Edge{
    
    public Node src;
    public Node dest;
    public double weight;
    
    public Edge(Node src, Node dest, double weight){
    this.src = src;
    this.dest = dest;
    this.weight = weight;
    }
    
    public Edge(Edge copy){
        this(copy.src, copy.dest, copy.weight);
    }
            
            
}
