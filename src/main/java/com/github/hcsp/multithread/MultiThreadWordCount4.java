package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 使用 Future与线程池 实现
 */
public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (File file : files) {
            futures.add(threadPool.submit(new WordCount(file)));
        }

        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> map = future.get();
            result = Util.mergeTwoMap(result, map);
        }
        threadPool.shutdown();
        return result;
    }

    static class WordCount implements Callable {
        File file;

        WordCount(File file) {
            this.file = file;
        }

        @Override
        public Object call() throws Exception {
            return Util.countWordFromOneFile(file);
        }
    }
}

