package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class WordCount {
    private int threadNum;
    // 统计文件中各单词的数量
    private ExecutorService threadPool;
    private BufferedReader reader;

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
        threadPool = Executors.newFixedThreadPool(threadNum);
    }

    public Map<String, Integer> count(File file) throws IOException, ExecutionException, InterruptedException {

        reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            futureList.add(threadPool.submit(new WorkJob()));
        }
        Map<String, Integer> finalMap = new HashMap<>();
        for (Future<Map<String, Integer>> future : futureList
        ) {
            Map<String, Integer> tmpMap = future.get();
            for (String key : tmpMap.keySet()
            ) {
                finalMap.put(key, tmpMap.get(key) + finalMap.getOrDefault(key, 0));
            }
        }
        return finalMap;
    }

    public class WorkJob implements Callable<Map<String, Integer>> {

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> wordMap = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
                }
            }
            return wordMap;
        }
    }
}
