package ru.bkmz.client;

import org.junit.Test;

/**
 * Created by bkmz on 21/07/16.
 */
public class TestClient {

    @Test
    public void testClient() throws InterruptedException {
        ReceiveClient receiveClient = new ReceiveClient(30);
        for (int i = 0; i < 100; i++) {
            receiveClient.send("some_message" + i);
        }

        System.out.println("send SIGINT to interrupt.");
        receiveClient.join();
    }
}
