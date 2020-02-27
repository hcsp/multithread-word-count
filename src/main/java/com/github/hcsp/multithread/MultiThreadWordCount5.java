package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * {@link java.util.concurrent.ForkJoinPool} implements
 */
public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        return forkJoinPool.submit(new CountTask(files)).get();
    }

    static class CountTask extends RecursiveTask<Map<String, Integer>> {

        List<File> fileList;

        CountTask(List<File> fileList) {
            this.fileList = fileList;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (fileList.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Integer> map1 = MultiThreadWordCount1.countOneFile(fileList.get(0));
            Map<String, Integer> map2 = new CountTask(fileList.subList(1, fileList.size())).compute();
            return merge(map1, map2);
        }
    }

    public static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> result = new HashMap<>();
        Set<String> keys = new HashSet<>();
        keys.addAll(map1.keySet());
        keys.addAll(map2.keySet());
        for (String key : keys) {
            result.put(key, map1.getOrDefault(key, 0) + map2.getOrDefault(key, 0));
        }
        return result;
    }
}
