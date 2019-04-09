package cleo.server;

import cleo.ConsoleUtils;
import cleo.connection.Connection;
import cleo.connection.Message;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) {
        Connection clientConnection = null;

        try (
            ServerSocket serverSocket = new ServerSocket(0)
        ) {
            ConsoleUtils.writeMessage("Started server on port " + serverSocket.getLocalPort());

            clientConnection = new Connection(serverSocket.accept());
            ConsoleUtils.writeMessage("Established connection with: " + clientConnection.getRemoteSocketAddress());

            while (true) {
                Message message = clientConnection.receive();

                if (message.getType() == Message.Type.TEXT) {
                    ConsoleUtils.writeMessage("Received message: " + message.getData());

                    ConsoleUtils.writeMessage("Sending it back");
                    clientConnection.send(message);
                }

            }

        } catch (IOException|ClassNotFoundException e) {
            ConsoleUtils.writeMessage(
                    "Ошибка при обмене данными с удаленным адресом " +
                    clientConnection.getRemoteSocketAddress() + ":\n" +
                    e.getMessage()
            );
        }
    }
}
