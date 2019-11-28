package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 使用Fork/Join框架实现协同读文件
 */
public class MultiThreadWordCount5 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<File> files = Arrays.asList(
                new File("1.txt"),
                new File("2.txt")
        );

        ForkJoinPool pool = new ForkJoinPool();
        // pool.submit(new WordCount(files)).get();
        System.out.println(pool.submit(new WordCount(files)).get());
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(File file) {
        Map<String, Integer> wordToCountMap = new HashMap<>();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    int count = wordToCountMap.getOrDefault(word, 0);
                    wordToCountMap.put(word, count + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordToCountMap;
    }

    private static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String word : map2.keySet()) {
            map1.put(word,
                    map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
        return map1;
    }

    static class WordCount extends RecursiveTask<Map<String, Integer>> {
        List<File> files;

        WordCount(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Integer> wordCountMap = count(files.get(0));
            Map<String, Integer> CountOfRestFilesMap = new WordCount(files.subList(1, files.size())).compute();
            return merge(wordCountMap, CountOfRestFilesMap);

        }
    }
}
