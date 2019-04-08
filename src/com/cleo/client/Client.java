package com.cleo.client;

import com.cleo.Connection;
import com.cleo.ConsoleUtils;

import java.io.*;
import java.net.Socket;

public class Client {

    private static int serverPort;
    private static String serverAddress = "127.0.0.1";

    public static void main(String[] args) {

        Socket serverSocket = null;

        try {
            ConsoleUtils.writeMessage("Please, enter server port: ");
            serverPort = ConsoleUtils.readInt();

            serverSocket = new Socket(serverAddress, serverPort);
            ConsoleUtils.writeMessage("Connected to server: " + serverSocket.getRemoteSocketAddress());

            Connection connection = new Connection(serverSocket);

            ConsoleUtils.writeMessage("Starting an endless conversation with server");
            String message;
            while (true) {
                ConsoleUtils.writeMessage("\nEnter your message, please: ");
                message = ConsoleUtils.readString();

                ConsoleUtils.writeMessage("Sending your message to server");
                connection.send(message);

                ConsoleUtils.writeMessage("Received this from server: " + connection.receive());
            }
        } catch (IOException|ClassNotFoundException e) {
            ConsoleUtils.writeMessage(
                    "Error communicating with remote address " +
                    serverSocket.getRemoteSocketAddress() + "\n" +
                    e.getMessage()
            );
        }
    }
}
