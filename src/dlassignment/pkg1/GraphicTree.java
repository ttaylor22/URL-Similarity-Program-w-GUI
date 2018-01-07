/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlassignment.pkg1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.util.ArrayList;

/**
 *
 * @author dt817
 */
public class GraphicTree extends JFrame {

    JScrollPane scrollpanel;
    drawingTree panel = null;
    GT t = new GT();
      
    GraphicTree(GT t, ArrayList<Node> path, ArrayList<String> ids){
        panel = new drawingTree(t,path,ids);
        panel.setPreferredSize(new Dimension(900,900));
        scrollpanel = new JScrollPane(panel);
        getContentPane().add(scrollpanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
    }
}