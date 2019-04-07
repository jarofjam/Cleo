package client;

import java.io.*;
import java.net.Socket;

public class Client {

    private static int serverPort;
    private static String serverAddress = "127.0.0.1";

    public static void main(String[] args) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.print("Please, enter server port: ");
            serverPort = Integer.parseInt(reader.readLine());

            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Connected to server: " + socket.getRemoteSocketAddress());

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Starting an endless conversation with server");
            String text;
            while (true) {
                System.out.println("\nEnter your message, please: ");
                text = reader.readLine();

                System.out.println("Sending your message to server");
                out.writeObject(text);

                System.out.println("Received this from server: " + in.readObject());
            }
        } catch (IOException|ClassNotFoundException e) {}
    }
}
