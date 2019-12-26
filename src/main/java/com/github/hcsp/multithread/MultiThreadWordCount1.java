package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        Map<String, Integer> result = new HashMap<>();

        for (File file : files) {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Future<Map<String, Integer>> future = executorService.submit(new CountWord(bufferedReader));
            futures.add(future);
        }

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> mapResult = future.get();

            for (String word : mapResult.keySet()) {
                result.put(word, result.getOrDefault(word, 0) + mapResult.get(word));
            }
        }

        return result;
    }


    private static class CountWord implements Callable<Map<String, Integer>> {

        private BufferedReader bufferedReader;

        private Map<String, Integer> result = new HashMap<>();

        public CountWord(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {

            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                String[] words = temp.split("\\s+");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            System.out.println("每个行统计" + result);
            return result;
        }
    }
}
