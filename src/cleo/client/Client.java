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

    private class ConnectionDaemon extends Thread {
        @Override
        public void run() {
            try (
                Socket socket = new Socket(getServerAddress(), getServerPort())
            ) {
                connection = new Connection(socket);
                notifyConnectionStatusChanged(true);

                processIncomingData();
            } catch (IOException|ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }

        private void processIncomingData() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() == Message.Type.TEXT)
                    displayMessage(message);
            }
        }

        private void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;

            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        private void displayMessage(Message message) {
            ConsoleUtils.writeMessage(message.getData());
        }
    }
}
