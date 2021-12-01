package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            ForkJoinTask<Map<String, Integer>> future = forkJoinPool.submit(new MultiThreadWordCount3.CountWorker(file));
            futures.add(future);
        }
        // 合并结果
        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            try {
                MultiThreadWordCount3.combineMap(result, future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static class CountWorker extends Thread {
        List<File> files;
        final Map<String, Integer> total;

        public CountWorker(List<File> files, Map<String, Integer> total) {
            this.files = files;
            this.total = total;
        }

        @Override
        public void run() {
            try {
                for (File file : files) {
                    List<String> strings = Files.readAllLines(file.toPath());
                    strings.stream()
                            .flatMap(v -> Arrays.stream(v.split(" ")))
                            .forEach(word -> {
                                synchronized (total) {
                                    total.computeIfPresent(word, (key, oldValue) -> oldValue++);
                                }
                            });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
