package ru.bkmz.client;

import com.google.common.util.concurrent.RateLimiter;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by bkmz on 21/07/16.
 */
public class ReceiveClient {

    private final Thread workerThread = new Thread(daemonWork());
    private final Logger log = LoggerFactory.getLogger(ReceiveClient.class);

    private RateLimiter rateLimiter;
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(10000);
    private AsyncHttpClient httpClient;

    public ReceiveClient(int qps) {
        rateLimiter = RateLimiter.create(qps);
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

    private ListenableFuture<Response> sendMessage(String message) {
        httpClient = new DefaultAsyncHttpClient();
        BoundRequestBuilder builder = httpClient.preparePost("http://localhost:8080/receive");
        builder.setBody(message);
        return builder.execute();
    }
}
