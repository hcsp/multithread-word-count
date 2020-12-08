package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用ForkJoinPool
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        ForkJoinTask<Map<String, Integer>> task = new readFileWordTask(files);
        Map<String, Integer> taskResults = forkJoinPool.invoke(task);
        return taskResults;
    }

    public static class readFileWordTask extends RecursiveTask<Map<String, Integer>> {
        private static final int THRESHOLD = 3;
        List<File> files;

        public readFileWordTask(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.size() <= 6) {
                Map<String, Integer> results = new ConcurrentHashMap<>();
                for (File file : files) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                results.put(word, results.getOrDefault(word, 0) + 1);
                            }
                        }
                        // System.out.println(file.getCanonicalFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return results;
            } else {
                List<File> subFiles1 = files.subList(0, files.size() / 2);
                List<File> subFiles2 = files.subList(files.size() / 2, files.size());
                readFileWordTask subTask1 = new readFileWordTask(subFiles1);
                readFileWordTask subTask2 = new readFileWordTask(subFiles2);
                invokeAll(subTask1, subTask2);
                Map<String, Integer> subResult1 = subTask1.join();
                Map<String, Integer> subResult2 = subTask2.join();
                return mergeTwoMap(subResult1, subResult2);
            }
        }

        private Map<String, Integer> mergeTwoMap(Map<String, Integer> subResult1, Map<String, Integer> subResult2) {
            Map<String, Integer> results = new ConcurrentHashMap<>(subResult2);
            for (Map.Entry<String, Integer> stringIntegerEntry : subResult1.entrySet()) {
                String word = stringIntegerEntry.getKey();
                int countNum = results.getOrDefault(word, 0) + stringIntegerEntry.getValue();
                results.put(word, countNum);
            }
            return results;
        }
    }
}
