package com.example.controlacc.model;

/**
 * @author Alfonso Hernandez Xochipa
 */
public class MessagesData {

    private String heading,messages;

    public MessagesData(String heading, String messages) {
        this.heading = heading;
        this.messages = messages;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
