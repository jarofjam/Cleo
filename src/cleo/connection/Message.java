package cleo.connection;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private final Type type;
    private final String data;
    private final Date created;

    public Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public Date getCreated() {
        return created;
    }

    public Message(Type type) {
        this.type = type;
        this.data = null;
        this.created = new Date();
    }

    public Message(Type type, String data) {
        this.type = type;
        this.data = data;
        this.created = new Date();
    }

    public String getTimestamp() {
        return "[" +
                    created.getHours() + ":" +
                    created.getMinutes() + ":" +
                    created.getSeconds() +
                "] ";
    }

    public enum Type {
        NAME_REQUEST,
        NAME_RESPONSE,
        NAME_ACCEPTED,
        TEXT,
        INFORMATION;
    }
}

