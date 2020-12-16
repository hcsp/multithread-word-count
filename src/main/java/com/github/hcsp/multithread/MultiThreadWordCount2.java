package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> count = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        long t0 = System.currentTimeMillis();
        for (File file : files) {
            futures.add(executorService.submit(() -> GetWordInFile.getWordCountFormFile(file)));
        }
        futures.forEach(future -> {
            try {
                Map<String, Integer> map = future.get();
                GetWordInFile.mapAdd(count, map);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        System.out.println("time" + (System.currentTimeMillis() - t0));
        return count;
    }
}
