package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {

        if (files == null) {
            return null;
        }

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        Map<String, Integer> map = forkJoinPool.submit(new fileswordcount(files)).get();
        return map;
    }

    private static Map<String, Integer> fileWordCount(File file) {
        Map<String, Integer> map = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    Integer aDefault = map.getOrDefault(word, 0);
                    map.put(word, aDefault + 1);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static Map<String, Integer> mergemap(Map<String, Integer> resultmap, Map<String, Integer> stringIntegerMap) {
        for (String s : stringIntegerMap.keySet()) {
            Integer map1OrDefault = stringIntegerMap.getOrDefault(s, 0);
            Integer reslutmapOrDefault = resultmap.getOrDefault(s, 0);
            resultmap.put(s, map1OrDefault + reslutmapOrDefault);
        }
        return resultmap;
    }

    static class fileswordcount extends RecursiveTask<Map<String, Integer>> {
        List<File> files;

         fileswordcount(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Integer> map0 = fileWordCount(files.get(0));
            Map<String, Integer> maptEnd = new fileswordcount(files.subList(1, files.size())).compute();
            return mergemap(map0, maptEnd);
        }
    }
}


