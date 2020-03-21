package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    public static void main(String[] args) {
        List<File> files = new ArrayList<>();
        files.add(new File("D:\\123\\1.TXT"));
        files.add(new File("D:\\123\\2.TXT"));
        files.add(new File("D:\\123\\3.TXT"));
        count(5, files);
    }

    private static ExecutorService threadPool;


    private static void merge(Map<String, Integer> resfromfuture, Map<String, Integer> finalres) {
        for (String key : resfromfuture.keySet()
        ) {
            finalres.put(key, finalres.getOrDefault(key, 0) + resfromfuture.get(key));
        }
    }


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        return count(files);
    }

    public static Map<String, Integer> count(List<File> files) {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalres = new HashMap<>();
        for (File file : files
        ) {
            Future<Map<String, Integer>> future = threadPool.submit(() -> count(file));
            futures.add(future);
        }
        for (Future<Map<String, Integer>> future : futures) {
            try {
                merge(future.get(), finalres);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return finalres;
    }

    public static Map<String, Integer> count(File file) throws IOException {
        Map<String, Integer> filemap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words
                ) {
                    filemap.put(word, filemap.getOrDefault(word, 0) + 1);
                }
            }
        }
        return filemap;
    }
}
