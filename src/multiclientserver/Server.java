/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiclientserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author H
 */
public class Server {

    private int port;
    public static ArrayList<Socket> listSK;

    public Server(int port) {
        this.port = port;
    }

    private void execute() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server is listening....");
        while (true) {
            Socket socket = server.accept();
            System.out.println("Received request from client : " + socket);
            Server.listSK.add(socket);
            ReadServer read = new ReadServer(socket);
            read.start();
        }

    }

    public static void main(String[] args) throws IOException {
        Server.listSK = new ArrayList<>();
        Server server = new Server(3000);
        server.execute();
    }
}

class ReadServer extends Thread {

    private Socket server;

    public ReadServer(Socket server) {
        this.server = server;
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(server.getInputStream());
            while (true) {
                String sms = dis.readUTF();
                for (Socket item : Server.listSK) {
                    if (item.getPort() != server.getPort()) {
                        DataOutputStream dos = new DataOutputStream(item.getOutputStream());
                        dos.writeUTF(server + " : "+sms);
                    }
                }
                System.out.println(server + " : "+sms);
            }

        } catch (Exception e) {
            System.out.println("client disconnect "+server);
            try {
                server.close();
                Server.listSK.remove(server);
            } catch (IOException ex) {
                System.out.println("Disconnect to Server");
            }
        }
    }
}
