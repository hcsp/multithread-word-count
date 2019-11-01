package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
    }

    // 统计文件中各单词的数量
    // 不使用多线程：2.8s; 使用多线程：3.4s ??？
    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException {
        HashMap<String, Integer> result = new HashMap<>();
        ArrayList<Future<Map<String, Integer>>> futures = new ArrayList<>();
        ArrayList<Map<String, Integer>> wordCountResults = new ArrayList<>();

        for (File file : files) {
            futures.add(threadPool.submit(() -> {
                Map<String, Integer> ret = new HashMap<>();
                try {
                    ret = getWordCountFromFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ret;
            }));
        }

        for (Future<Map<String, Integer>> future : futures) {
            wordCountResults.add(future.get());
        }

        return this.mergeWordCountResult(wordCountResults);
    }

    public Map<String, Integer> mergeWordCountResult(List<Map<String, Integer>> results) {
        Map<String, Integer> finalResult = new HashMap<>();
        for (Map<String, Integer> ret : results) {
            for (Map.Entry<String, Integer> entry : ret.entrySet()) {
                String word = entry.getKey();
                Integer count = entry.getValue();
                Integer oldCount = ret.get(word);

                ret.put(word, count + (oldCount != null ? oldCount : 0));
            }
        }
        return finalResult;
    }

    public Map<String, Integer> getWordCountFromFile(File file) throws IOException {
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();

        while (line != null) {
            line = line.toLowerCase();
            String[] words = line.split("\\s+");

            for (String word : words) {
                result.merge(word, 1, Integer::sum);
            }

            line = reader.readLine();
        }
        return result;
    }
}
