package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 使用 ForkJoinPool 实现
 */
public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.submit(new WordCount(files)).get();
    }

    static class WordCount extends RecursiveTask<Map<String, Integer>> {
        List<File> list;

        WordCount(List<File> list) {
            this.list = list;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (list.isEmpty()) {
                return Collections.emptyMap();
            }
            try {
                Map<String, Integer> map1 = Util.countWordFromOneFile(list.get(0));
                Map<String, Integer> map2 = new WordCount(list.subList(1, list.size())).compute();
                return Util.mergeTwoMap(map1, map2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
