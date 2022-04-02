package es.udc.redes.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class WebServer {

    public static int Port;
    public static String Dir;
    public static String DirIndex;
    public static boolean permitir;

    public static void main(String[] args) {

        ServerSocket socket = null;

        try {
            Port = Integer.parseInt(args[0]);
            Dir = "p1-files/";

            socket = new ServerSocket(Port);
            socket.setSoTimeout(300000);

            while (true){
                Socket ClientSocket = socket.accept();
                ServerThread serverThread = new ServerThread(ClientSocket, Dir);
                serverThread.start();
            }

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    
}
