package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newFixedThreadPool;


public class WordCount {
    private ExecutorService executorService;

    public WordCount(int threadNum) {
        executorService = newFixedThreadPool(threadNum);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = executorService.submit(() -> {
                Map<String, Integer> countMap = new HashMap<>();
                try {
                    FileReader reader = new FileReader(file);
                    BufferedReader br = new BufferedReader(reader);
                    String temp;
                    while ((temp = br.readLine()) != null) {
                        String[] strs = temp.split("\\s");
                        for (String str : strs) {
                            Integer value = countMap.getOrDefault(str, 0);
                            countMap.put(str, value + 1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return countMap;
            });
            futureList.add(future);
        }
        Map<String, Integer> result = new HashMap<>();
        for (Future future : futureList) {
            Map sub = (Map) future.get();
            mergerMap(result, sub);
        }
        return result;
    }

    private void mergerMap(Map mainMap, Map subMap) {
        Set<Map.Entry<String, Integer>> set = subMap.entrySet();
        set.stream().forEach(entry -> {
            String key = entry.getKey();
            Integer value = (Integer) mainMap.getOrDefault(entry.getKey(), 0);
            mainMap.put(key, value + entry.getValue());
        });
    }
}
