package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
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
            futures.add(threadPool.submit(countWord(file)));
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

    private static Callable<Map<String, Integer>> countWord(File file) throws IOException {
        Callable<Map<String, Integer>> callable = new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                Map<String, Integer> map = new HashMap<>();
                List<String> contents = FileUtils.readFileContent(file);
                for (String content : contents) {
                    String[] words = content.split(" ");
                    for (String word : words) {
                        synchronized (MultiThreadWordCount1.class) {
                            map.put(word, map.getOrDefault(word, 0) + 1);
                        }
                    }
                }
                return map;
            }
        };
        return callable;
    }

}
