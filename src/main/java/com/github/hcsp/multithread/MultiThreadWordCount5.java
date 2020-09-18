package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
       public static Map<String, Integer> count(int threadNum, List<File> files) {
           final Semaphore parseSemaphore = new Semaphore(threadNum);
           final Semaphore mergeSemaphore = new Semaphore(0);

           Map<String, Integer> result = new ConcurrentHashMap<>();

            Thread mergeThread = new Thread(() -> {
                try {
                    mergeSemaphore.acquire(threadNum);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("可以返回咯");
                parseSemaphore.release(threadNum);
            });


           mergeThread.start();

           ExecutorService executor = Executors.newFixedThreadPool(2);

           for (File file : files) {
               executor.submit(() -> {
                   try {
                       parseSemaphore.acquire();
                   } catch (InterruptedException e) {
                       Thread.currentThread().interrupt();
                   }
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
                   mergeSemaphore.release();
               });
           }

           try {
               mergeThread.join();
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
           }

           return result;
       }
}
