package es.udc.redes.webserver;

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;


public class ServerThread extends Thread {

    public enum Codigos {
        OK("200 OK"),

        NOT_MODIFIED("304 Not Modified"),

        BAD_REQUEST("400 Bad Request"),

        NOT_FOUND("404 Not Found"),

        NOT_IMPLEMENTED("501 Not implemented");

        private final String estado;

        private Codigos(String code) {
            this.estado = code;
        }

        public String getEstado() {
            return estado;
        }
    }

    private Codigos code;
    private Date date;
    private File file = null;
    private String dirCodes;
    private String dirArchives;
    private final SimpleDateFormat formato = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
    private final Socket socket;
    private final String Dir;

    public ServerThread(Socket s, String d) {
        // Store the socket s
        this.socket = s;
        this.Dir = d;
    }


    public void run() {

        dirCodes = Dir + "codes"; //ruta de la carpeta de errores
        dirArchives = Dir + "archivos"; //ruta de la carpeta de archivos

        BufferedReader reader = null;
        PrintWriter writer = null;
        String fileRequest = null;
        BufferedOutputStream out = null;

        try {
          // This code processes HTTP requests and generates
          // HTTP responses

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Mensaje recibido
            writer = new PrintWriter(socket.getOutputStream());
            out = new BufferedOutputStream(socket.getOutputStream()); //Mensaje enviado

            String cabecera = null;
            String mensaje = reader.readLine();
            String aux = reader.readLine();

            if (mensaje != null){
                StringTokenizer parse = new StringTokenizer(mensaje);
                String metodo = parse.nextToken().toUpperCase(); //Comprueba si es un get o un head
                fileRequest = parse.nextToken().toLowerCase(); //Archivo que pide el cliente

                if (!metodo.equals("GET") && !metodo.equals("HEAD")){
                    code = Codigos.BAD_REQUEST;
                    printHeader(writer, out, "", fileRequest);
                }
                if (metodo.equals("GET")){
                    printHeader(writer, out, metodo, fileRequest);
                }
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (out != null) {
                    out.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void printHeader(PrintWriter printer, OutputStream dataOut, String metodo, String content) throws IOException {
        File requested = new File(dirArchives + "/" + content);

        if (code == Codigos.BAD_REQUEST) {
            file = new File(dirCodes + "/error400.html");
        } else {
            if (requested.exists()) {//si el archivo que me pidio el cliente existe lo creo
                code = Codigos.OK;
                file = new File(dirArchives + content);
            }
        }

        date = new Date();//fecha
        int length = (int) file.length();//tamaño archivo
        byte[] data = readerData(file, length);//archivo solicitado por el cliente
        //CABECERA DE RESPUESTA
        printer.println("HTTP/1.0 " + code.getEstado());
        printer.println("Date: " + date);
        printer.println("Server: WebServer_58L");
        printer.println("Content-Length: " + file.length());
        printer.println("Content-Type: " + getType(file));
        printer.println("Last-Modified: " + formatDate((int) file.lastModified()));
        printer.println("");
        printer.flush();
        //Envio respuesta
        dataOut.write(data, 0, length);
        dataOut.flush();
    }


    private String formatDate(int seconds) { //Da formato a la fecha
        return formato.format(seconds);
    }

    private Date parseDate(String fecha) {//Cambia de tipo String a fecha
        Date parsedDate = null;
        try {
            parsedDate = formato.parse(fecha);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return parsedDate;
    }

    private byte[] readerData(File file, int lentgh) throws IOException {//lee los archivos para después mandarlos
        FileInputStream input = null;
        byte[] data = new byte[lentgh];

        try {
            input = new FileInputStream(file);
            input.read(data);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return data;
    }

    private String getType(File fileRequest) {//Identifica el tipo de archivo
        String request = fileRequest.getName();
        String type = "application/octet-stream";

        if (request.endsWith(".htm") || request.endsWith(".html")) {
            type = "text/html";
        }
        if (request.endsWith(".log") || request.endsWith(".txt")) {
            type = "text/plain";
        }
        if (request.endsWith(".gif")) {
            type = "image/gif";
        }
        if (request.endsWith(".png")) {
            type = "image/png";
        }
        return type;
    }

}
