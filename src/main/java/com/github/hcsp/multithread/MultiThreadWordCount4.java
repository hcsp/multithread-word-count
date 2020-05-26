package com.github.hcsp.multithread;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool实现
 */
public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        try {
            return forkJoinPool.submit(new WordCount(files)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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
            Map<String, Integer> wordCountMap = Utils.countOneFile(files.get(0));
            Map<String, Integer> restWordCountMap = new WordCount(files.subList(1, files.size())).compute();
            Map<String, Integer> result = new HashMap<>();
            Utils.mergeSourceMapToDest(result, wordCountMap);
            Utils.mergeSourceMapToDest(result, restWordCountMap);
            return result;
        }
    }
}
