package com.github.hcsp.multithread;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        // 线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> fu = threadPool.submit(new CountWorker(file));
            futures.add(fu);
        }
        // 合并结果
        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            try {
                combineMap(result, future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static class CountWorker implements Callable<Map<String, Integer>> {
        File file;

        public CountWorker(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> map = new HashMap<>();
            List<String> strings = Files.readAllLines(file.toPath());
            strings.stream()
                    .flatMap(v -> Arrays.stream(v.split(" ")))
                    .forEach(word -> {
                        Integer integer = map.get(word);
                        map.put(word, integer == null ? 1 : (integer + 1));
                    });
            return map;
        }
    }

    public static void combineMap(Map<String, Integer> result, Map<String, Integer> map) {
        map.forEach((key, value) -> {
            int count = result.get(key) == null ? 0 : result.get(key);
            result.put(key, count + value);
        });
    }
}
