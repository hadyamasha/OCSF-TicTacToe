package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class GameEvent {
    private Message message;

    public GameEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}