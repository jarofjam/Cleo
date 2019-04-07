package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class Server {

    private static int port;

    public static void main(String[] args) {
        try (
            ServerSocket serverSocket = new ServerSocket(0)
        ) {
            port = serverSocket.getLocalPort();

            System.out.println("Started server on port: " + port);

            System.out.println("Waiting for a client...");
            Socket clientSocket = serverSocket.accept();

            System.out.println("Established connection with: " + clientSocket.getRemoteSocketAddress());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Switching to responding mode");
            String text;
            while (true) {
                System.out.println("\nAwaiting any input message");

                text = (String) in.readObject();
                System.out.println("Received message: " + text);

                System.out.println("Sending it back");
                out.writeObject(text);
                out.flush();
            }

        } catch (IOException|ClassNotFoundException e) {}
    }
}
