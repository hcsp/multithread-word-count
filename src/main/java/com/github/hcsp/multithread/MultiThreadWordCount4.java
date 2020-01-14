package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = files.stream()
                .map(file -> threadPool.submit(() -> Common.countOneFile(file)))
                .collect(Collectors.toList());

        return getResultMap(futures);
    }

    private static Map<String, Integer> getResultMap(List<Future<Map<String, Integer>>> futures) {
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).reduce(new HashMap<>(), Common::mergeMaps);
    }
}
