package com.example.weyland.service;

import com.example.weyland.dto.CommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class CommandQueueWorker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CommandQueueWorker.class);
    private final BlockingQueue<CommandRequest> queue;
    private final int workerId;
    private final Consumer<String> completionCallback;
    private final Runnable updateQueueSize;

    public CommandQueueWorker(
            BlockingQueue<CommandRequest> queue,
            int workerId,
            Consumer<String> completionCallback,
            Runnable updateQueueSize
    ) {
        this.queue = queue;
        this.workerId = workerId;
        this.completionCallback = completionCallback;
        this.updateQueueSize = updateQueueSize;
    }

    @Override
    public void run() {
        while (true) {
            try {
                CommandRequest command = queue.take();
                log.info("Worker #{} executing COMMON command: {}", workerId, command);
                Thread.sleep(1000);
                completionCallback.accept(command.getAuthor());
                updateQueueSize.run();
            } catch (InterruptedException e) {
                log.error("Worker #{} interrupted", workerId, e);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
