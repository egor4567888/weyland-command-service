package com.example.weyland.service;

import com.example.weyland.ReactorControl;
import com.example.weyland.dto.CommandRequest;
import com.example.weyland.exception.QueueOverflowException;
import com.example.weyland.model.Priority;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CommandService {

    private static final Logger log = LoggerFactory.getLogger(CommandService.class);
    private static final int QUEUE_CAPACITY = 100;
    private static final int THREAD_POOL_SIZE = 2;

    private final BlockingQueue<CommandRequest> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);


    private final MeterRegistry meterRegistry;
    private final AtomicInteger queueSizeGauge = new AtomicInteger(0);
    private final ConcurrentHashMap<String, Counter> authorCounters = new ConcurrentHashMap<>();

    public CommandService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;


        Gauge.builder("android.tasks.queue.size", queueSizeGauge, AtomicInteger::get)
                .description("Current number of tasks in the queue")
                .register(meterRegistry);
    }

    @PostConstruct
    public void initWorkerThreads() {
        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            executorService.submit(new CommandQueueWorker(queue, i + 1, this::incrementAuthorCounter, this::updateQueueSize));
        }

    }


    public void handleCommand(CommandRequest command) {
        if (command.getPriority() == Priority.CRITICAL) {
            executeCriticalCommand(command);
            incrementAuthorCounter(command.getAuthor());
        } else if (!queue.offer(command)) {
            throw new QueueOverflowException("Command queue is full");
        }
        updateQueueSize();

    }

    public void executeCriticalCommand(CommandRequest command) {
        log.info("Executing CRITICAL command: {}", command);
    }

    private void updateQueueSize() {
        queueSizeGauge.set(queue.size());
    }

    private void incrementAuthorCounter(String author) {
        authorCounters
                .computeIfAbsent(author, key -> Counter.builder("android.tasks.completed.by.author")
                        .description("Number of completed tasks by author")
                        .tag("author", key)
                        .register(meterRegistry))
                .increment();
    }

}
