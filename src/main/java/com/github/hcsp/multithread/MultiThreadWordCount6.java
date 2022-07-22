package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        /**
         * 安全:
         * 协同:
         * Future + ExecutorService(ExecutorService)
         */
        int step = files.size() / threadNum;
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            futureList.add(executorService.submit(new WordCounter6(files.subList(i * step, i == threadNum - 1 ? files.size() : (i + 1) * step))));
        }

        Map<String, Integer> result = new ConcurrentHashMap<>();
        for (Future<Map<String, Integer>> future : futureList) {
            Map<String, Integer> stringIntegerMap = future.get();
            stringIntegerMap.forEach((key, val) -> result.merge(key, val, Integer::sum));
        }

        executorService.shutdown();

        return result;
    }
}
