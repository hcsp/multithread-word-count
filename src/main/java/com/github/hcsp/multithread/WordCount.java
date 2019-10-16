package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {
    private Integer threadNum;
    private ExecutorService executorService;

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
        executorService = Executors.newFixedThreadPool(threadNum);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        Map<String, Integer> allResults = new HashMap<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
//            List<Map<String, Integer>> results = new ArrayList<>();
            for (int i = 0; i < threadNum; i++) {
                Future<Map<String, Integer>> future = executorService.submit(new Callable<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call() throws Exception {
                        String line;
                        Map<String, Integer> result = new HashMap<>();
                        while ((line = reader.readLine()) != null) {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                result.put(word, result.getOrDefault(word, 0) + 1);
                            }
                        }
                        return result;
                    }
                });
                for (Map.Entry<String, Integer> entry : future.get().entrySet()) {
                    String word = entry.getKey();
                    allResults.put(word, allResults.getOrDefault(word, 0) + entry.getValue());
                }

            }
        }
        return allResults;
    }
}


