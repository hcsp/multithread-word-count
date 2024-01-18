package com.github.hcsp.multithread;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(threadNum);
        ForkJoinTask<Map<String, Integer>> task = new Task(files);
        pool.submit(task);
        return task.get();
    }

    public static class Task extends RecursiveTask<Map<String, Integer>> {
        private final List<File> files;

        public Task(List<File> files) {
            this.files = files;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (files.size() == 1) {
                try {
                    return doWork();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            List<File> subFiles1 = new ArrayList<>();
            List<File> subFiles2 = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (i % 2 == 0) {
                    subFiles1.add(file);
                } else {
                    subFiles2.add(file);
                }
            }
            Task task1 = new Task(subFiles1);
            Task task2 = new Task(subFiles2);
            invokeAll(task1, task2);
            return mergeTask(task1.join(), task2.join());
        }

        private Map<String, Integer> doWork() throws IOException {
            Map<String, Integer> result = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(files.get(0)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }
            }
            return result;
        }

        private static Map<String, Integer> mergeTask(Map<String, Integer> t1, Map<String, Integer> t2) {
            Map<String, Integer> result = new HashMap<>(t1);
            for (Map.Entry<String, Integer> entry : t2.entrySet()) {
                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
            return result;
        }
    }

}
