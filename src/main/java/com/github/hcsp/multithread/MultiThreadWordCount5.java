package com.github.hcsp.multithread;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool 实现
 *
 * @author kwer
 * @date 2020/5/5 22:33
 */
public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(10);
        ForkJoinTask<Map<String, Integer>> forkJoinTask = forkJoinPool.submit(new WordCountTask(files));
        return forkJoinTask.get();
    }

    static class WordCountTask extends RecursiveTask<Map<String, Integer>> {
        List<File> files;

        public WordCountTask(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Integer> map1 = WordCountUtil.count(files.get(0));
            Map<String, Integer> map2 = new WordCountTask(files.subList(1, files.size())).compute();
            return WordCountUtil.merge(map1, map2);
        }
    }
}
