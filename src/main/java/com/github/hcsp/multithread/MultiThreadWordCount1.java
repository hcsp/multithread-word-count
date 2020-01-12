package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (int i = 0; i < files.size(); i++) {
            ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(files.get(i)));
                for (int j = 0; j < threadNum; j++) {
                    futures.add(threadPool.submit(new CountWork(reader)));
                }
            } catch (FileNotFoundException e) {
                 throw new WordCountFileNotFoundException("count(int threadNum, List<File> files) 发生了异常",e);
            }
        }

        for (Future<Map<String, Integer>> future : futures) {
            mergerFurturesResult(finalResult, future);
        }

        return finalResult;
    }


    static class CountWork implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        CountWork(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = this.reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    static class WordCountFileNotFoundException extends RuntimeException{
        public WordCountFileNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static void mergerFurturesResult(Map<String, Integer> finalResult, Future<Map<String, Integer>> future) {
        try {
            Map<String, Integer> resultTemp = future.get();
            for (Map.Entry<String, Integer> entry : resultTemp.entrySet()) {
                String word = entry.getKey();
                int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
                finalResult.put(word, mergedResult);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
