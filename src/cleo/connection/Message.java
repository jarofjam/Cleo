package cleo.connection;

import java.io.Serializable;

public class Message implements Serializable {

    private final Type type;
    private final String data;

    public Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public Message(Type type) {
        this.type = type;
        this.data = null;
    }

    public Message(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    public enum Type {
        TEXT;
    }
}

