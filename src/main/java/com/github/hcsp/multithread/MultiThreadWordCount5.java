package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        return forkJoinPool.submit(new WordCount(files)).get();
    }

    static class WordCount extends RecursiveTask<Map<String, Integer>> {
        private List<File> files;

        WordCount(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.isEmpty()){
                return new HashMap<String, Integer>();
            }
            try {
                Map<String, Integer> wordCount = count(files.get(0));
                Map<String, Integer> countOfRestFiles = new WordCount(files.subList(1, files.size())).compute();
                return merge(wordCount, countOfRestFiles);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
            map1.keySet().forEach(key -> {
                if (map2.containsKey(key)) {
                    map2.put(key, map2.get(key) + map1.get(key));
                } else {
                    map2.put(key, map1.get(key));
                }
            });
            return map2;
        }

        private Map<String, Integer> count(File file) throws IOException {
            Map<String, Integer> result = new HashMap<>();
            List<String> allLines = Files.readAllLines(file.toPath());
            for (String line : allLines) {
                String[] words = line.split(" ");
                Arrays.asList(words).forEach(word -> {
                    int count = result.getOrDefault(word, 0);
                    result.put(word, ++count);
                });
            }
            return result;
        }
    }
}
