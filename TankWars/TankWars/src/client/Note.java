/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Erik
 */
public class Note extends JPanel implements Runnable{
    private Thread t;
    private JLabel l;
    private long delay;
    
    Note(String m, long delay){
        this.delay = delay;
        this.setBackground(Color.red);
        this.setSize(300,300);
        t = new Thread(this,"Note");
        this.setVisible(true);
        l = new JLabel(m);
        this.add(l);
        l.setPreferredSize(new Dimension(300,300));
        t.start();
    }
    
    
    @Override
    public void run() {
    this.setVisible(true);
    this.repaint();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {
            Logger.getLogger(Note.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
