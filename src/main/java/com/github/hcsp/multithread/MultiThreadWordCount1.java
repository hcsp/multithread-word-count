package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池实现wordcount
 */

public class MultiThreadWordCount1 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
            Future<Map<String, Integer>> future = threadPool.submit(new WorkJob(file));
            futures.add(future);
        }
        //接受结果
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> result = future.get();
            FileUtil.merge(result, finalResult);
        }
        return finalResult;
    }

    static class WorkJob implements Callable<Map<String, Integer>> {
        private File file;

        WorkJob(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() {
            return FileUtil.work(file);
        }
    }

}
