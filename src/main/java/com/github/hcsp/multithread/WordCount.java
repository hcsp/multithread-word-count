package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    private final int threadNum;
    private ExecutorService executorService;

    public WordCount(int threadNum) {
        executorService = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;

    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> result = new HashMap<>();
        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            futures.add(executorService.submit(new FileReadJob(bufferedReader)));
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> fileWordsCount = future.get();
            mergeFileWordsCountInfoResult(fileWordsCount, result);
        }
        return result;
    }

    private void mergeFileWordsCountInfoResult(Map<String, Integer> fileWordsCount, Map<String, Integer> result) {
        for (Map.Entry<String, Integer> entry : fileWordsCount.entrySet()) {
            int mergeValue = entry.getValue() + result.getOrDefault(entry.getKey(), 0);
            result.put(entry.getKey(), mergeValue);
        }
    }

    static class FileReadJob implements Callable<Map<String, Integer>> {
        private BufferedReader bufferedReader;

        FileReadJob(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> lineWordsCount = new HashMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    lineWordsCount.put(word, lineWordsCount.getOrDefault(word, 0) + 1);
                }
            }
            return lineWordsCount;
        }
    }
}
