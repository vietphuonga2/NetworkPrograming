/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiclientserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author H
 */
public class Client {
    private InetAddress host;
    private int port;
    public Client(InetAddress host,int port){
        this.host =host;
        this.port =port;
    }
    
    private void execute() throws IOException{
        Socket client =new Socket(host, port);
        ReadClient read =new ReadClient(client);
        read.start();
        WriteClient write =new WriteClient(client);
        write.start();
    }
    public static void main(String[] args) throws IOException {
        Client client =new Client(InetAddress.getLocalHost(), 3000);
        client.execute();
    }
}
class ReadClient extends Thread{
    private Socket client;

        public ReadClient(Socket client) {
            this.client = client;
        }
    @Override
    public void run(){
        DataInputStream dis =null;
        try {
            dis =new DataInputStream(client.getInputStream());
            while(true){
                String sms =dis.readUTF();
                System.out.println(sms);
            }
        } catch (Exception e) {
            try {
                dis.close();
                client.close();
            } catch (IOException ex) {
                System.out.println("Disconnect to Server");
            }
        }
    }
}
class WriteClient extends Thread{
    private Socket client;

        public WriteClient(Socket client) {
            this.client = client;
        }
    @Override
    public void run(){
        DataOutputStream dos =null;
        Scanner sc =new Scanner(System.in);
        try {
            dos =new DataOutputStream(client.getOutputStream());
            while(true){
                String sms =sc.nextLine();
                dos.writeUTF(sms);
            }
        } catch (Exception e) {
            try {
                dos.close();
                client.close();
            } catch (IOException ex) {
                System.out.println("Disconnect to Server");
            }
        }
    }
}
