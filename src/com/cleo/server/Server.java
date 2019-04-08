package com.cleo.server;

import com.cleo.Connection;
import com.cleo.ConsoleUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class Server {

    private static int port;

    public static void main(String[] args) {
        Socket clientSocket = null;

        try (
            ServerSocket serverSocket = new ServerSocket(0)
        ) {
            port = serverSocket.getLocalPort();

            ConsoleUtils.writeMessage("Started server on port: " + port);

            ConsoleUtils.writeMessage("Waiting for a client...");
            clientSocket = serverSocket.accept();

            ConsoleUtils.writeMessage("Established connection with: " + clientSocket.getRemoteSocketAddress());
            Connection connection = new Connection(clientSocket);

            ConsoleUtils.writeMessage("Switching to responding mode");
            String message;
            while (true) {
                ConsoleUtils.writeMessage("\nWaiting for any input message");

                message = connection.receive();
                ConsoleUtils.writeMessage("Received message: " + message);

                ConsoleUtils.writeMessage("Sending it back");
                connection.send(message);
            }

        } catch (IOException|ClassNotFoundException e) {
            ConsoleUtils.writeMessage(
                    "Error communicating with remote address " +
                    clientSocket.getRemoteSocketAddress() + "\n" +
                    e.getMessage()
            );
        }
    }
}
