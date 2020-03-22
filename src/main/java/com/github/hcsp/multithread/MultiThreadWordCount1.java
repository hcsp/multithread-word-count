package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 采用线程池和future的方法
 */

public class MultiThreadWordCount1 {
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        //将每一个文件的结果汇总加起来
        long start = System.currentTimeMillis();
        HashMap<String, Integer> resultEnd = new HashMap<>();
        for (File f : files
        ) {
            Map<String, Integer> result = count1(threadNum, f);
            for (String key : result.keySet()) {
                resultEnd.put(key, resultEnd.getOrDefault(key, 0) + result.getOrDefault(key, 0));
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        return resultEnd;
    }

    //使用Future与线程池
    public static Map<String, Integer> count1(int threadNum, File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        //从线程池找中新建threadNum个线程
        ExecutorService service = Executors.newFixedThreadPool(threadNum);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        //为每一个线程分配任务，结果保留在Future,将每个线程的Future汇总到list中
        for (int i = 0; i < threadNum; i++) {
            futures.add(service.submit(() -> {
                String line;
                Map<String, Integer> result = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    String[] wards = line.split(" ");
                    for (String word : wards) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }
                return result;
            }));
        }

        //将同一文件不同线程的统计结果汇总
        Map<String, Integer> resultAll = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultSub = future.get();
            for (String key : resultSub.keySet()) {
                resultAll.put(key, resultAll.getOrDefault(key, 0) + resultSub.get(key));
            }
        }
        return resultAll;
    }
}

