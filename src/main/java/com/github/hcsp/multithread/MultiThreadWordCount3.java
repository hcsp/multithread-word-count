package com.github.hcsp.multithread;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<String> words;
        try (Stream<String> wordsStream = files.parallelStream().flatMap(MultiThreadWordCount1::apply)) {
            words = wordsStream.collect(Collectors.toList());
        }
        CountDownLatch latch = new CountDownLatch(words.size());
        Lock lock = new ReentrantLock();
        for (String word : words) {
            threadPool.submit(() -> {
                try {
                    lock.lock();
                    result.put(word, result.getOrDefault(word, 0) + 1);
                } catch (Exception ignored) {

                } finally {
                    lock.unlock();
                }
                latch.countDown();
                return result;
            });
        }
        latch.await();
        return result;
    }

    private static Stream<String> apply(File file) throws IOException {
        return Files.readAllLines(file.toPath()).stream().map(stringList -> stringList.split("\\s+")).
                flatMap(Arrays::stream);
    }
}
