package com.github.hcsp.multithread;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

/**
 * 使用ExcutorService与Future实现线程协同
 */
public class MultiThreadWordCount4 {
    public static void main(String[] args) {
        List<File> files = Arrays.asList(
                new File("1.txt"),
                new File("2.txt")
        );
        // count(files.size(), files);
        System.out.println(count(files.size(), files));
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> finalResultMap = new HashMap<>();

        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = threadPool.submit(new WordCount(file));
            futures.add(future);
        }

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> wordCountMap = null;
            try {
                wordCountMap = future.get();
                merge(finalResultMap, wordCountMap);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        threadPool.shutdown();
        return finalResultMap;
    }

    private static void merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String word : map2.keySet()) {
            map1.put(word,
                    map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
    }

    static class WordCount implements Callable<Map<String, Integer>> {
        File file;

        public WordCount(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            List<String> lines = Files.readAllLines(file.toPath());
            Map<String, Integer> wordToCountMap = new HashMap<>();

            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    int count = wordToCountMap.getOrDefault(word, 0);
                    wordToCountMap.put(word, count + 1);
                }
            }
            return wordToCountMap;
        }
    }
}
