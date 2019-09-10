package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        this.threadNum = threadNum;
        threadPool = Executors.newFixedThreadPool(threadNum);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws IOException, ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new ScanLine(reader)));
        }

        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultOfLine = future.get();
            mergeResultOfLineIntoFinalResult(resultOfLine, finalResult);
        }
        return finalResult;
    }

    private void mergeResultOfLineIntoFinalResult(Map<String, Integer> resultOfLine, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultOfLine.entrySet()) {
            String word = entry.getKey();
            int numOfWord = entry.getValue();

            int mergedNum = finalResult.getOrDefault(word, 0) + numOfWord;

            finalResult.put(word, mergedNum);
        }
    }

    private class ScanLine implements Callable<Map<String, Integer>> {
        BufferedReader reader;

        ScanLine(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> numOfWordsInLine = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] wordsInLine = line.split(" ");
                for (String word : wordsInLine) {
                    numOfWordsInLine.put(word, numOfWordsInLine.getOrDefault(word, 0) + 1);
                }
            }
            return numOfWordsInLine;
        }
    }
}
