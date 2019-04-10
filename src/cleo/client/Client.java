package cleo.client;

import cleo.ConsoleUtils;
import cleo.connection.Connection;
import cleo.connection.Message;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        new Client().run();
    }

    public void run() {
        createConnectionDaemon().start();

        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                ConsoleUtils.writeMessage(e.getMessage());
                return;
            }
        }

        if (clientConnected) {
            ConsoleUtils.writeMessage("Connection established. Type 'exit' to exit.");
        } else {
            ConsoleUtils.writeMessage("An error has occurred.");
        }

        while (clientConnected) {
            String textMessage = ConsoleUtils.readString();

            if("exit".equalsIgnoreCase(textMessage))
                break;

            sendTextMessage(textMessage);
        }
    }

    private ConnectionDaemon createConnectionDaemon() {
        ConnectionDaemon connectionDaemon = new ConnectionDaemon();
        connectionDaemon.setDaemon(true);
        return connectionDaemon;
    }

    private void sendTextMessage(String messageText) {
        try {
            connection.send(new Message(Message.Type.TEXT, messageText));
        } catch (IOException e) {
            ConsoleUtils.writeMessage("Error sending message:\n\t" + messageText);
            clientConnected = false;
        }
    }

    private String getServerAddress() {
        return "127.0.0.1";
    }

    private int getServerPort() {
        ConsoleUtils.writeMessage("Enter server port:");
        return ConsoleUtils.readInt();
    }

    private String getUserName() {
        ConsoleUtils.writeMessage("Enter username");
        return ConsoleUtils.readString();
    }

    private class ConnectionDaemon extends Thread {
        @Override
        public void run() {
            try (
                Socket socket = new Socket(getServerAddress(), getServerPort())
            ) {
                connection = new Connection(socket);

                clientHandshake();
                processIncomingData();

            } catch (IOException|ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }

        private void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message request = connection.receive();

                if (request.getType() == Message.Type.NAME_REQUEST) {
                    if (request.getData() != null)
                        informAboutProblem(request.getData());

                    connection.send(new Message(Message.Type.NAME_RESPONSE, getUserName()));
                    continue;
                }

                if (request.getType() == Message.Type.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    break;
                }

                throw new IOException("Unexpected message type");
            }
        }

        private void informAboutProblem(String message) {
            ConsoleUtils.writeMessage(message);
        }

        private void processIncomingData() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() == Message.Type.TEXT)
                    displayMessage(message);

                if (message.getType() == Message.Type.INFORMATION)
                    displayInformation(message);
            }
        }

        private void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;

            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        private void displayMessage(Message message) {
            ConsoleUtils.writeMessage("[" +
                    message.getCreated().getHours() + ":" +
                    message.getCreated().getMinutes() + ":" +
                    message.getCreated().getSeconds() + "] " +
                    message.getData());
        }

        private void displayInformation(Message message) {
            ConsoleUtils.writeMessage(
                    "\u001B[33m[" +
                    message.getCreated().getHours() + ":" +
                    message.getCreated().getMinutes() + ":" +
                    message.getCreated().getSeconds() + "] " +
                    message.getData() +
                    "\u001B[0m"
            );
        }
    }
}
