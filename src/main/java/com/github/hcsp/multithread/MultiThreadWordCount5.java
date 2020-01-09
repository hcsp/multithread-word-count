package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> result = new HashMap<>();

        for (File file : files) {
            threadPool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            synchronized (result) {
                                result.put(word, result.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                    return null;
                }
            });
        }


        threadPool.shutdown();
//        threadPool.awaitTermination(1, TimeUnit.MINUTES);

        return result;
    }
}
