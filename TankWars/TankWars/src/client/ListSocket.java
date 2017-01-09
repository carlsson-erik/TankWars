package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import serverList.ListUser;
import serverList.ServerData;
import serverList.UserPackage;

/**
 *
 * @author Erik
 */
public class ListSocket implements Runnable {

    private Thread t;

    private Socket socket;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ArrayList<ServerData> servers;

    public ListSocket(String ip, int port) {
        t = new Thread(this, "ListSocket");
        try {
            socket = new Socket(InetAddress.getByName(ip), port);
        } catch (IOException ex) {
            Logger.getLogger(ListSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        servers = new ArrayList();
        t.start();
    }

    @Override
    public void run() {
        System.out.println("setting up streams");
        setupStreams();
        System.out.println("Connected");
        whileConnected();
        System.out.println("Cloing streams");
        closeStreams();
        System.out.println("Streams closed");

    }

    public void setupStreams() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ListUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void whileConnected() {
        do {
            try {
                servers.add((ServerData) input.readObject());
                System.out.println(servers.size());
            } catch (IOException ex) {
                Logger.getLogger(ListUser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ListUser.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (true);
    }

     public void closeStreams() {
        try {
            output.close();
            input.close();
        } catch (IOException ex) {
            Logger.getLogger(ListUser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void output(UserPackage u) {
        try {
            output.writeObject(u);
        } catch (IOException ex) {
            Logger.getLogger(ListSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    synchronized public void clearServers(){
        servers.clear();
    }

    /**
     * @return the servers
     */
    synchronized public ArrayList<ServerData> getServers() {
        return servers;
    }

}
