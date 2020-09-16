package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiThreadWordCount1 {
    private static ExecutorService executorService;

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        // 初始化executor service
        init(threadNum);

        // 多线程执行
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(executorService.submit(new WorkCountTask(file)));
        }

        // 获取每个线程的结果并合并
        Map<String, Integer> result = new HashMap<>();
        try {
            for (Future<Map<String, Integer>> future : futures) {
                mergeMap(result, future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static Map<String, Integer> mergeMap(Map<String, Integer> result, Map<String, Integer> map) {
        map.forEach((key, val) -> result.merge(key, val, Integer::sum));
        return result;
    }

    private static void init(int threadNum) {
        executorService = new ThreadPoolExecutor(threadNum, threadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(2900));
    }
}
