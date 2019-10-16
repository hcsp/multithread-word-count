package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {
    private ExecutorService executorService;

    public WordCount(int threadNum) {
        executorService = Executors.newFixedThreadPool(threadNum);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) throws ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        // 每个文件用一个线程去获取
        for (File item : file) {
            futures.add(executorService.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    Map<String, Integer> result = new HashMap<>();
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(item));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            // 如果这个key在Map中存在，就+1；否则在这个Map新加key，数量+1
                            if (result.containsKey(word)) {
                                result.put(word, result.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                    return result;
                }
            }));
        }
        // 获取线程结果
        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> item : futures) {
            Map<String, Integer> futureResult = item.get();
            for (Map.Entry<String, Integer> entry : futureResult.entrySet()) {
                String word = entry.getKey();
                int quantity = result.getOrDefault(word, 0) + entry.getValue();
                result.put(word, quantity);
            }
        }
        return result;
    }


}
