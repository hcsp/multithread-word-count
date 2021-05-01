package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> map = new HashMap<>();
        ForkJoinPool pool = new ForkJoinPool(threadNum);
        for (File file : files) {
            Map<String, Integer> result = pool.invoke(new Task(file));
            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                Integer num = map.getOrDefault(entry.getKey(), 0);
                map.put(entry.getKey(), num + entry.getValue());
            }
        }

        return map;
    }

    static class Task extends RecursiveTask<Map<String, Integer>> {
        private File file;

        @Override
        protected Map<String, Integer> compute() {
            try {
                return readFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private Map<String, Integer> readFile(File file) throws Exception {
            Map<String, Integer> result = new HashMap<>();
            BufferedReader in = new BufferedReader(new FileReader(file));
            String strLine = null;
            while ((strLine = in.readLine()) != null) {
                List<String> words = Arrays.asList(strLine.split("\\s"));
                for (String word : words) {
                    Integer num = result.getOrDefault(word, 0);
                    num++;
                    result.put(word, num);
                }
            }
            return result;
        }

        public Task(File file) {
            this.file = file;
        }
    }
}

