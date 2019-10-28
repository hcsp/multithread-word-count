package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {

    private int threadNum;
    private ExecutorService executorService;

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
        executorService = Executors.newFixedThreadPool(threadNum);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) throws ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (File fileItem : file) {
            futures.add(executorService.submit(new WorkJob(fileItem)));
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultItem = future.get();
            mergeResultItemIntoFinalResult(finalResult, resultItem);
        }

        return finalResult;
    }

    private void mergeResultItemIntoFinalResult(Map<String, Integer> finalResult, Map<String, Integer> resultItem) {
        for (Map.Entry<String, Integer> entry : resultItem.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }

    static class WorkJob implements Callable<Map<String, Integer>> {

        File file;

        WorkJob(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> result = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
