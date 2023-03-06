package marmara;

import java.io.*;
import java.net.Socket;

public class SocketHandler implements Runnable {
    private final Socket socket;
    private final Database db;

    /**
     * Creates a request handler
     *
     * @param socket the socket that will be handled.
     * @param db     is the database
     */
    public SocketHandler(Socket socket, Database db) {
        this.socket = socket;
        this.db = db;
    }

    /**
     * Runs the handling procedure
     */
    public void run() {
        try {
            String s;

            //Prepare string lines coming from socket
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Be prepared to write lines to incoming socket
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //Read incoming first line
            if ((s = in.readLine()) == null) return;

            //run the request handler
            new RequestHandler(s, out, db);

            //The job is done, free resources
            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
