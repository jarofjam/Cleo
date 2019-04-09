package cleo.server;

import cleo.ConsoleUtils;
import cleo.connection.Connection;
import cleo.connection.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try (
            ServerSocket serverSocket = new ServerSocket(0)
        ) {
            ConsoleUtils.writeMessage("Started server on port " + serverSocket.getLocalPort());

            while (true) {
                ClientConnectionHandler handler = new ClientConnectionHandler(serverSocket.accept());
                handler.start();
            }

        } catch (Exception e) {
            ConsoleUtils.writeMessage(e.getMessage());
        }
    }

    private static class ClientConnectionHandler extends Thread {
        private Socket socket;

        ClientConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleUtils.writeMessage("A new connection has been established with " + socket.getRemoteSocketAddress());

            try (
                Connection connection = new Connection(socket)
            ) {
                processIncomingData(connection);

            } catch (IOException|ClassNotFoundException e) {
                ConsoleUtils.writeMessage(
                        "Ошибка при обмене данными с удаленным адресом " +
                                socket.getRemoteSocketAddress() + ":\n" +
                                e.getMessage()
                );
            }
        }

        private void processIncomingData(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() == Message.Type.TEXT)
                    ConsoleUtils.writeMessage("Received message from: " + connection.getRemoteSocketAddress());
                    ConsoleUtils.writeMessage("\t" + message.getData());

                    ConsoleUtils.writeMessage("Sending it back");
                    connection.send(message);
            }
        }
    }
}
