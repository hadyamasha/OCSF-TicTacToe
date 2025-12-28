package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class SimpleClient extends AbstractClient {

    private static SimpleClient client = null;

    private SimpleClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        // If the message is a Game Message, post it so the Controller can see it
        if (msg instanceof Message) {
            EventBus.getDefault().post(new GameEvent((Message) msg));
        }
    }

    public static SimpleClient getClient() {
        if (client == null) {

            client = new SimpleClient("192.168.10.107", 3000);
        }
        return client;
    }
}