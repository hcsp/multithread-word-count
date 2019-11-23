package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, InterruptedException {
        Lock lock = new ReentrantLock();
        Condition finishResult = lock.newCondition();
        AtomicInteger size = new AtomicInteger(files.size());
        Map<String, Integer> result = new ConcurrentHashMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            threadPool.submit(() -> {
                try {
                    lock.lock();
                    List<String> words =
                            Files.readAllLines(file.toPath()).stream().flatMap(line -> Arrays.stream(line.split("\\s+"))).collect(Collectors.toList());
                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    finishResult.signalAll();
                    size.decrementAndGet();
                    lock.unlock();
                }
                return result;
            });
        }
        lock.lock();
        try {
            if (size.get() > 0) {
                finishResult.await();
            }
        } catch (Exception ignored) {

        } finally {
            lock.unlock();
        }
        return result;
    }
}

