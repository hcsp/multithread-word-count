package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

//使用Future与线程池

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用Future与线程池

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = threadPool.submit(new WordCount(file));
            futureList.add(future);
        }
        Map<String, Integer> finalResult = new HashMap<>();
        List<Map<String, Integer>> list = new ArrayList<>();
        for (Future<Map<String, Integer>> future : futureList) {
            try {
                Map<String, Integer> result = future.get();
                list.add(result);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }
        return CountUtil.mergeFileResult(list);

    }

    static class WordCount implements Callable<Map<String, Integer>> {
        File file;

        WordCount(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            return CountUtil.countOneFile(file);
        }
    }
}
