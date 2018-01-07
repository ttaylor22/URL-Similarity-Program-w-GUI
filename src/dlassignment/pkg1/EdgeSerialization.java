/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;


import java.io.Serializable;

/**
 *
 * @author dt817
 */
public class EdgeSerialization implements Serializable{
    
    public String src;
    public String dest;
    public double weight;
    
    public EdgeSerialization (String src, String dest, double weight){
    this.src = src;
    this.dest = dest;
    this.weight = weight;
    }
    
            
}
