package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        final CyclicBarrier barrier = new CyclicBarrier(threadNum + 1);
        Map<String, Integer> result = new HashMap<>();
        for (File file : files) {
            new Thread(() -> {
                FileReader fileReader = null;
                try {
                    fileReader = new FileReader(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("FileNotFoundException", e);
                }
                BufferedReader bf = new BufferedReader(fileReader);
                Map<String, Integer> collect = new HashMap<>();
                final List<String> strList = bf.lines().collect(Collectors.toList());
                for (String line : strList) {
                    final String[] keys = line.split("\\s+");
                    for (String key : keys) {
                        final Integer val = collect.getOrDefault(key, 0);
                        collect.put(key, val + 1);
                    }
                }
                MultiThreadWordCount1.mergeMap(result, collect);
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException("BrokenBarrierException", e);
                }
            }).start();
        }
        try {
            barrier.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException("BrokenBarrierException", e);
        }
        return result;
    }

}
