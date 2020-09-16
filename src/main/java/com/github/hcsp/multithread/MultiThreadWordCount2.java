package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class MultiThreadWordCount2 {
    static LinkedBlockingQueue<Future<Map<String, Integer>>> queue;

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        queue = new LinkedBlockingQueue<>(files.size() >> 1);


        Map<String, Integer> result = new HashMap<>();
        final Thread consumerThread = new Thread(() -> {
            new WorkCountConsumer<>(queue).consumer(task -> MultiThreadWordCount1.mergeMap(result, task));
        });

        consumerThread.start();

        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            try {
                queue.put(executor.submit(new WorkCountTask(file)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            queue.put((Future) PoisonPill.INSTANCE);
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        return result;
    }

    static class WorkCountConsumer<T> {
        private LinkedBlockingQueue<Future<T>> queue;

        WorkCountConsumer(LinkedBlockingQueue<Future<T>> queue) {
            this.queue = queue;
        }

        public void consumer(Consumer<T> consumer) {
            Future<T> future = null;
            while (true) {
                try {
                    future = this.queue.poll(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

                    if (future == PoisonPill.INSTANCE) {
                        return;
                    }
                    consumer.accept(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    enum PoisonPill implements Future<Object> {
        INSTANCE;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDone() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            throw new UnsupportedOperationException();
        }
    }
}
