
package client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Erik
 */
public class ServerItem extends JPanel{
    
    
    
    private JLabel ip,players,port,name;
    private JButton button;
    
    public ServerItem(String name, int players, String ip,int port){
        
        this.ip = new JLabel(ip);
        this.name = new JLabel(name);
        this.players = new JLabel(Integer.toString(players));
        this.port = new JLabel(Integer.toString(port));
        
        button = new JButton(name);
        button.setPreferredSize(new Dimension(100,25));
    
        this.setLayout(new FlowLayout(0));
        
    this.add(button);
    this.add(this.name);
    this.add(this.players);
    this.add(this.ip);
    this.add(this.port);
    
    }
    
    
    
}
