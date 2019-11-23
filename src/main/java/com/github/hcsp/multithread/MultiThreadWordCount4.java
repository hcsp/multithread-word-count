package com.github.hcsp.multithread;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static com.github.hcsp.multithread.Count.wordsCount;

public class MultiThreadWordCount4 {
    //     使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException,
            InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        ForkJoinTask<Map<String, Integer>> forkJoinTask = forkJoinPool.submit(new WordCount(files));
        return forkJoinTask.get();
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
            try {
                Map<String, Integer> wordCount = wordsCount(files.get(0));
                Map<String, Integer> countOfRestFiles = new WordCount(files.subList(1, files.size())).compute();
                return merge(wordCount, countOfRestFiles);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Map<String, Integer> merge(Map<String, Integer> wordCount, Map<String, Integer> countOfRestFiles) {
            Set<String> words = new HashSet<>(wordCount.keySet());
            words.addAll(countOfRestFiles.keySet());
            Map<String, Integer> result = new HashMap<>();
            for (String word : words) {
                result.put(word, wordCount.getOrDefault(word, 0) + countOfRestFiles.getOrDefault(word, 0));
            }
            return result;
        }
    }
}
