package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
        public static Map<String, Integer> count(int threadNum, List<File> files) {
            ExecutorService threadPool = Executors.newFixedThreadPool(files.size());
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();
            files.forEach(file -> {
                futures.add(threadPool.submit(() -> MultiThreadWordCount1.countOneFile(file)));
            });

            List<Map<String, Integer>> mapList = futures.stream().map(mapFuture -> {
                try {
                    return mapFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            return MultiThreadWordCount1.merge(mapList);
        }
}
