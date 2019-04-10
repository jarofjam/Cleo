package cleo.server;

import cleo.ConsoleUtils;
import cleo.connection.Connection;
import cleo.connection.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<String, Connection>();

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

    public static void sendBroadcastText(String text) {
        Message message = new Message(Message.Type.TEXT, text);
        try {
            for (Map.Entry<String, Connection> user : connectionMap.entrySet()) {
                user.getValue().send(message);
            }
        } catch (IOException e) {
            ConsoleUtils.writeMessage("Error sending message: \n" + e.getMessage());
        }
    }

    private static void sendBroadcastInformation(String information) {
        Message message = new Message(Message.Type.INFORMATION, information);
        try {
            for (Map.Entry<String, Connection> user : connectionMap.entrySet()) {
                user.getValue().send(message);
            }
        } catch (IOException e) {
            ConsoleUtils.writeMessage("Error sending information: \n" + e.getMessage());
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

            String userName = null;
            try (
                Connection connection = new Connection(socket)
            ) {
                userName = serverHandshake(connection);
                sendBroadcastInformation(userName + " joined the group");

                processIncomingData(userName, connection);

            } catch (IOException|ClassNotFoundException e) {
                ConsoleUtils.writeMessage(
                        "Error communicating with remote address " +
                        socket.getRemoteSocketAddress() + ":\n" +
                        e.getMessage()
                );
            } finally {
                if (userName != null) {
                    connectionMap.remove(userName);
                    sendBroadcastInformation(userName + " left the group");
                }
            }
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            connection.send(new Message(Message.Type.NAME_REQUEST));

            while(true) {
                Message response = connection.receive();

                if (response.getType() != Message.Type.NAME_RESPONSE) {
                    connection.send(new Message(Message.Type.NAME_REQUEST, "Unexpected message type"));
                    continue;
                }

                String userName = response.getData();
                if (userName == null || "".equals(userName)) {
                    connection.send(new Message(
                            Message.Type.NAME_REQUEST, "Invalid username"));
                    continue;
                }

                if (connectionMap.containsKey(userName)) {
                    connection.send(new Message(Message.Type.NAME_REQUEST, "Username already taken"));
                    continue;
                }

                connectionMap.put(userName, connection);
                connection.send(new Message(Message.Type.NAME_ACCEPTED));

                return userName;
            }
        }

        private void processIncomingData(String userName, Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() != Message.Type.TEXT) {
                    ConsoleUtils.writeMessage(
                            "Incoming data processing error." +
                            "Unexpected message type (message from " + userName + ")");
                    continue;
                }

                sendBroadcastText(userName + ": " + message.getData());
            }
        }
    }
}
