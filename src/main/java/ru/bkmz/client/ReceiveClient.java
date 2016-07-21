package ru.bkmz.client;

import com.google.common.util.concurrent.RateLimiter;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by bkmz on 21/07/16.
 */
public class ReceiveClient {

    private final Thread workerThread = new Thread(daemonWork());
    private final Logger log = LoggerFactory.getLogger(ReceiveClient.class);

    private RateLimiter rateLimiter;
    private final Client client;
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10000);

    public ReceiveClient(int qps) {
        rateLimiter = RateLimiter.create(qps);
        client = new JerseyClientBuilder().build();
        workerThread.start();
    }

    public void send(String message) {
        log.debug("adding to queue" + message);
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Runnable daemonWork() {
        return () -> {
            while (true) {
                String message = null; // block and wait
                try {
                    message = queue.take();
                } catch (InterruptedException e) {
                    log.debug("Interrupted! " + e);
                }
                log.debug("sending " + message);
                rateLimiter.acquire();
                sendMessage(message);
            }
        };
    }

    private Response sendMessage(String message) {
        WebTarget webTarget = client.target("http://localhost:8080").path("receive");
        Invocation invocation = webTarget.request()
                .buildPost(Entity.entity(message, MediaType.TEXT_PLAIN_TYPE));

        Response response = invocation.invoke();
        log.debug("Response is: {}", response.getStatus());
        return response;
    }

    public void join() {
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
