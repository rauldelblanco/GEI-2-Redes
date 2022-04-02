package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {

    public static void main(String argv[]) throws IOException {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }

        ServerSocket TCPSocket = null;

        try {
            // Create a server socket
            TCPSocket = new ServerSocket(Integer.parseInt(argv[0]));
            // Set a timeout of 300 secs
            TCPSocket.setSoTimeout(30000);
            while (true) {
                // Wait for connections
                Socket socket = TCPSocket.accept();
                // Set the input channel
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // Set the output channel
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                // Receive the client message
                String mensaje = in.readLine();
                System.out.println("SERVER: Received " +  mensaje + " from " + socket.getLocalAddress()+ ":" + socket.getPort());
                // Send response to the client
                out.println(mensaje);
                System.out.println("SERVER: Sending " +  mensaje + " from " + socket.getLocalAddress() + ":" + socket.getPort());
                // Close the streams
                socket.close();
                in.close();
                out.close();
            }
        // Uncomment next catch clause after implementing the logic            
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
	        //Close the socket
            TCPSocket.close();
        }
    }
}
