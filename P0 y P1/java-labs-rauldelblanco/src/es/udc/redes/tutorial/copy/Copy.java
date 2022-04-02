package es.udc.redes.tutorial.copy;
import java.io.*;

public class Copy {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Format: es.udc.redes.tutorial.copy.Copy <fichero origen> <fichero destino>");
            System.exit(-1);
        }

        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(new FileReader(args[0]));
            out = new BufferedWriter(new FileWriter(args[1]));
            int c;

            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }

    }
    
}
