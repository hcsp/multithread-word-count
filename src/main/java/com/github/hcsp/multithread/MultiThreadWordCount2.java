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
        return forkJoinPool.invoke(task);
    }

    public static class readFileWordTask extends RecursiveTask<Map<String, Integer>> {
        private static final int THRESHOLD = 5;
        List<File> files;

        public readFileWordTask(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            Map<String, Integer> results = null;
            if (files.size() < THRESHOLD) {
                for (File file : files) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        results = new ConcurrentHashMap<>();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                results.put(word, results.getOrDefault(word, 0) + 1);
                            }
                        }
                        System.out.println(file.getCanonicalFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                List<File> subFiles1 = files.subList(0, files.size() / 2);
                List<File> subFiles2 = files.subList(files.size() / 2, files.size());
                readFileWordTask subTask1 = new readFileWordTask(subFiles1);
                readFileWordTask subTask2 = new readFileWordTask(subFiles2);
                invokeAll(subTask1, subTask2);
                Map<String, Integer> subResult1 = subTask1.join();
                Map<String, Integer> subResult2 = subTask2.join();
                results = mergeTwoMap(subResult1, subResult2);
            }
            return results;
        }

        private Map<String, Integer> mergeTwoMap(Map<String, Integer> subResult1, Map<String, Integer> subResult2) {
            Map<String, Integer> results = new ConcurrentHashMap<>(subResult1);
            for (Map.Entry<String, Integer> stringIntegerEntry : subResult2.entrySet()) {
                String word = stringIntegerEntry.getKey();
                results.put(word, subResult2.getOrDefault(word, 0) + 1);
            }
            return results;
        }
    }

    public static void main(String[] args) throws IOException {
        List<File> files = new ArrayList<>();
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\1.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\2.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\3.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\4.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\5.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\6.txt"));
        Map<String, Integer> results = MultiThreadWordCount2.count(5, files);
        for (Map.Entry<String, Integer> resultEntry : results.entrySet()) {
            System.out.println("Key: " + resultEntry.getKey()+"    Value: " + resultEntry.getValue());
        }
    }
}
