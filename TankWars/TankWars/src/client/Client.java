
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import serverList.ServerData;
import serverList.UserPackage;

/**
 *
 * @author Erik
 */
public class Client extends JFrame implements Runnable, ActionListener {

    JLayeredPane lPane;
    //panels
    private JPanel startP, findServerP, createServerP, optionsP, gameP, notificationP;

    //Start Panel 
    private JButton createServer, joinServer, options, exit, back;

    //findServerPanel
    private JButton refresh, join;
    private JScrollPane scrollPane;

    //createServerPanel
    private JButton create;
    private JLabel serverNameLabel, maxPlayersLabel;
    private JTextField serverName, maxPlayers;
    private JPanel serversP;

    //notification panel
    private JLabel notifyMessage;

    private Thread t;
    private boolean running;

    private String ip;
    private int port;

    //ServerList
    private ListSocket listSocket;

    //File containing server information
    private File configFile;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        t = new Thread(this, "Client");
        running = true;
        createAndShowGUI();

        findAndReadConfigFile();
        System.out.println(ip);
        System.out.println(port);
        listSocket = new ListSocket(ip, port);

        t.start();
    }

    public void createAndShowGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(500, 500));
        this.setBackground(Color.yellow);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        //startPanel components
        createServer = new JButton("Create Server");

        joinServer = new JButton("Join Server");
        options = new JButton("Options");
        exit = new JButton("Exit");
        back = new JButton("Back");

        //create panels
        startP = new JPanel();
        startP.setSize(500, 500);
        startP.setLayout(new FlowLayout(1));
        startP.setBackground(Color.white);
        startP.setVisible(true);

        findServerP = new JPanel();
        findServerP.setSize(500, 500);
        findServerP.setBackground(Color.white);
        findServerP.setVisible(false);

        serversP = new JPanel();
        serversP.setLayout(new FlowLayout(0));
        serversP.setPreferredSize(new Dimension(400, 2000));

        gameP = new JPanel();
        gameP.setVisible(false);
        gameP.setSize(new Dimension(500, 500));
        gameP.setBackground(Color.red);

        notificationP = new JPanel();
        notificationP.setBounds(0, 0, 200, 50);
        notificationP.setBackground(Color.red);
        notificationP.setOpaque(true);
        notificationP.setVisible(false);
        notifyMessage = new JLabel("Test");

        scrollPane = new JScrollPane(serversP);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.setPreferredSize(new Dimension(300, 300));

        lPane = new JLayeredPane();

        createServerP = new JPanel();
        createServerP.setSize(500, 500);
        createServerP.setBackground(Color.white);
        createServerP.setVisible(false);

        optionsP = new JPanel();
        optionsP.setSize(500, 500);
        optionsP.setBackground(Color.white);
        optionsP.setVisible(false);

        //findServer panel components
        join = new JButton("Join");
        refresh = new JButton("Refresh");

        //createServer panel components
        serverNameLabel = new JLabel("Server Name:");
        maxPlayersLabel = new JLabel("Players:");

        maxPlayers = new JTextField();
        maxPlayers.setPreferredSize(new Dimension(100, 25));
        serverName = new JTextField();
        serverName.setPreferredSize(new Dimension(100, 25));

        create = new JButton("Create");

        createServer.addActionListener(this);
        joinServer.addActionListener(this);
        options.addActionListener(this);
        exit.addActionListener(this);
        back.addActionListener(this);
        join.addActionListener(this);
        refresh.addActionListener(this);
        create.addActionListener(this);

        this.add(lPane, BorderLayout.CENTER);

        lPane.add(notificationP, BorderLayout.CENTER, 0);
        lPane.add(startP, BorderLayout.NORTH, 1);
        lPane.add(findServerP, BorderLayout.NORTH);
        lPane.add(createServerP, BorderLayout.NORTH);
        lPane.add(optionsP, BorderLayout.NORTH);
        lPane.add(gameP, BorderLayout.CENTER);

        notificationP.add(notifyMessage);

        startP.add(joinServer);
        startP.add(createServer);
        startP.add(options);
        startP.add(exit);

        findServerP.add(join);
        findServerP.add(refresh);
        findServerP.add(scrollPane);

        createServerP.add(serverNameLabel);
        createServerP.add(serverName);
        createServerP.add(maxPlayersLabel);
        createServerP.add(maxPlayers);
        createServerP.add(create);

    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(listSocket.getServers().size() > serversP.getComponentCount()){
                serversP.removeAll();
                for(ServerData s : listSocket.getServers()){
                    serversP.add(new ServerItem(s.getName(),s.getPlayers(),s.getIp(),s.getPort()));
                }
            }
            this.revalidate();
            serversP.revalidate();
        }
    }

    public void findAndReadConfigFile() {
        try {
            configFile = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "\\config.txt");
        } catch (URISyntaxException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(configFile);
        readConfigFile();
    }

    public void readConfigFile() {

        if (configFile != null) {
            Scanner scanner = null; //this is requierd to read from a file
            try {
                scanner = new Scanner(configFile); // Tries to read from a textfile
            } catch (IOException e) {
                System.out.println("A text file was created. Put ip,port");

                byte[] list = new byte[]{0, 0};
                try {
                    try {
                        Files.write(Paths.get(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "config.txt"), list);
                    } catch (IOException ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (URISyntaxException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }

                startP.setVisible(false);
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(","); //checks for , on the line and splits it into different commands and puts it into a array

                ip = parts[0]; // The following part reads from the array
                port = Integer.parseInt(parts[1]);

            }

        }

    }

    public void createServer() {

    }

    public void createGame() {

    }

    public void notify(String m) {
        notificationP.add(new Note("hello", 1000));

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String com = e.getActionCommand();

        switch (com) {
            case "Join Server":
                notify("Hello");
                startP.setVisible(false);
                findServerP.setVisible(true);
                createServerP.setVisible(false);
                optionsP.setVisible(false);
                findServerP.add(back);
                gameP.setVisible(false);

                break;
            case "Create Server":
                startP.setVisible(false);
                findServerP.setVisible(false);
                createServerP.setVisible(true);
                optionsP.setVisible(false);
                createServerP.add(back);
                gameP.setVisible(false);

                break;
            case "Options":
                startP.setVisible(false);
                findServerP.setVisible(false);
                createServerP.setVisible(false);
                optionsP.setVisible(true);
                optionsP.add(back);
                gameP.setVisible(false);

                break;
            case "Back":

                startP.setVisible(true);
                findServerP.setVisible(false);
                createServerP.setVisible(false);
                optionsP.setVisible(false);
                gameP.setVisible(false);

                break;
            case "Exit":
                System.exit(0);
                break;
            case "Join":

                break;
            case "Refresh":
                listSocket.clearServers();
                listSocket.output(new UserPackage(true));
                
                break;
            case "Create":
                createGame();

                startP.setVisible(false);
                findServerP.setVisible(false);
                createServerP.setVisible(false);
                optionsP.setVisible(false);
                startP.add(back);
                gameP.setVisible(true);
                break;
        }

    }

}
