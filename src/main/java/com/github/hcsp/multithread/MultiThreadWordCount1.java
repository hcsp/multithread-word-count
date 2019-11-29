package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {

        if (files == null) {
            return null;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        ConcurrentHashMap<String, Integer> reslutmap = new ConcurrentHashMap<>();
        ArrayList<Future<ConcurrentHashMap<String, Integer>>> futures = new ArrayList<>();

        for (File file : files) {
            Future<ConcurrentHashMap<String, Integer>> future = executorService.submit(new countfile(file));
            futures.add(future);
        }

        for (Future<ConcurrentHashMap<String, Integer>> future : futures) {
            ConcurrentHashMap<String, Integer> concurrentHashMap1 = future.get();
            for (String s : concurrentHashMap1.keySet()) {
                Integer map1OrDefault = concurrentHashMap1.getOrDefault(s, 0);
                Integer reslutmapOrDefault = reslutmap.getOrDefault(s, 0);
                reslutmap.put(s, map1OrDefault + reslutmapOrDefault);
            }
        }
        return reslutmap;
    }

    static class countfile implements Callable<ConcurrentHashMap<String, Integer>> {
        File file;

         countfile(File file) {
            this.file = file;
        }

        @Override
        public ConcurrentHashMap<String, Integer> call() throws Exception {
            ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    String[] words = line.split("\\s+");

                    for (String word : words) {
                        Integer aDefault = concurrentHashMap.getOrDefault(word, 0);
                        concurrentHashMap.put(word, aDefault + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return concurrentHashMap;
        }
    }

}
