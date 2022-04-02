package es.udc.redes.tutorial.udp.server;

import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Implements a UDP echo server.
 */
public class UdpServer {

    public static void main(String[] argv) {

        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }

        DatagramSocket rDatagram = null;
        try {
            // Create a server socket
            rDatagram = new DatagramSocket(Integer.parseInt(argv[0]));
            // Set maximum timeout to 300 secs
            rDatagram.setSoTimeout(300000);
            while (true) {
                // Prepare datagram for reception
                byte[] buffer = new byte[1024];
                DatagramPacket dgramReceived = new DatagramPacket(buffer, buffer.length);
                // Receive the message
                rDatagram.receive(dgramReceived);
                String mensaje = new String(dgramReceived.getData(),0,dgramReceived.getLength());
                int puertoCliente = dgramReceived.getPort();
                InetAddress dir = dgramReceived.getAddress();
                System.out.println("SERVER: Received " + mensaje + " from /" + dir + ":" + puertoCliente);
                // Prepare datagram to send response
                buffer = mensaje.getBytes();
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length, dir,puertoCliente);
                // Send response
                rDatagram.send(respuesta);
                System.out.println("SERVER: Sending " + mensaje + " to /" + dir + ":" + puertoCliente);
            }
          
        // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
        // Close the socket
        rDatagram.close();
        }
    }
}
