package com.github.hcsp.multithread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
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

public class WordCount {
    private ExecutorService service;
    private int threadSize;

    public WordCount(int threadNum) {
        service = Executors.newFixedThreadPool(threadNum);
        threadSize = threadNum;
    }

    // 统计文件中各单词的数量
    Map<String, Integer> count(List<File> files) throws FileNotFoundException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadSize; ++i) {
                futures.add(service.submit(() -> {
                    Map<String, Integer> callMap = new HashMap<>();
                    String line;
                    while (true) {
                        try {
                            if ((line = reader.readLine()) == null) {
                                break;
                            }
                            String[] words = line.split(" ");
                            for (String word : words) {
                                callMap.put(word, callMap.getOrDefault(word, 0) + 1);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return callMap;
                }));
            }
        }

        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> cached = future.get();
                merge(cached, result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private void merge(Map<String, Integer> cached, Map<String, Integer> result){
        for (Map.Entry<String, Integer> entry : cached.entrySet()){
            result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }


}

