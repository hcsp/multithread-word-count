package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static com.github.hcsp.multithread.MultiThreadWordCount1.countSingleFile;

/**
 * 使用{@link ForkJoinPool} 实现多线程的WordCount
 */
public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(threadNum);
        return pool.submit(new CountFile(files)).get();
    }

    /**
     * 可以递归进行的单个子任务
     */
    static class CountFile extends RecursiveTask<Map<String, Integer>> {
        List<File> files;

        CountFile(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.isEmpty()) {
                return Collections.emptyMap();
            }

            Map<String, Integer> wordCount = countSingleFile(files.get(0));
            Map<String, Integer> forkResult = new CountFile(files.subList(1, files.size())).compute();

            return joinMap(wordCount, forkResult);
        }

        private Map<String, Integer> joinMap(Map<String, Integer> map1, Map<String, Integer> map2) {
            Set<String> words = new HashSet<>(map1.keySet());
            words.addAll(map2.keySet());

            Map<String, Integer> result = new HashMap<>();
            for (String word : words) {
                result.put(word, map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
            }
            return result;
        }
    }
}
