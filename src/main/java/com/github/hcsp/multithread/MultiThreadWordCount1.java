package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        ExecutorService threadPool = newFixedThreadPool(threadNum);
        for (File file : files) {
            if (!file.exists()) {
                System.out.println("文件不存在");
                continue;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new Task(bufferedReader)));
            }
        }
        for (Future<Map<String, Integer>> future : futures) {
            for (Map.Entry<String, Integer> entry : future.get().entrySet()) {
                Integer num = map.getOrDefault(entry.getKey(), 0);
                map.put(entry.getKey(), num + entry.getValue());
            }
        }
        threadPool.shutdown();
        return map;
    }

    static class Task implements Callable<Map<String, Integer>> {
        private BufferedReader bufferedReader;

        public Task(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String strLine = null;
            Map<String, Integer> countMap = new ConcurrentHashMap<>();
            while (null != (strLine = bufferedReader.readLine())) {
                String[] words = strLine.split("\\s");
                for (String word : words) {
                    Integer num = countMap.getOrDefault(word, 0);
                    num++;
                    countMap.put(word, num);
                }
            }
            return countMap;
        }
    }
}
