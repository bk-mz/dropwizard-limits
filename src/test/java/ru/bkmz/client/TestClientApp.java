package ru.bkmz.client;

/**
 * Created by bkmz on 21/07/16.
 */
public class TestClientApp {

    public static void main(String[] args) {
        ReceiveClient receiveClient = new ReceiveClient(29);
        for (int i = 0; i < 100; i++) {
            receiveClient.send("some_message" + i);
        }
    }
}
