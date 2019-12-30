package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Stream;

public class MultiThreadWordCount3 {
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(11);
    public static Map<String, Integer> wordCounts = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws BrokenBarrierException, InterruptedException {

        for (int i = 0; i < threadNum; i++) {
            File file = files.get(i);
            new Thread(() -> {
                try {
                    Stream<String> lines = Files.lines(Paths.get(file.getPath()));
                    lines.forEach((words) -> {
                        String[] aWords = words.split("\\s");
                        List<String> aWordList = Arrays.asList(aWords);
                        aWordList.forEach((key) -> {
                            wordCountPut(key);
                        });
                    });
                    cyclicBarrier.await();
                } catch (IOException | InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        cyclicBarrier.await();
        return wordCounts;
    }

    private static void wordCountPut(String key) {
        synchronized (MultiThreadWordCount3.class) {
            wordCounts.put(key, wordCounts.getOrDefault(key, 0) + 1);
        }
    }
}
