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
        ExecutorService es = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> lastResult = new HashMap<>();
        for (File file : files) {
            ArrayList<Future<Map<String, Integer>>> futures = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(es.submit(new work(br)));
            }

            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> workMap = future.get();
                mergeWorkMap(workMap, lastResult);

            }
        }

        return lastResult;
    }

    private static void mergeWorkMap(Map<String, Integer> workMap, Map<String, Integer> lastResult) {
        for (Map.Entry<String, Integer> entry : workMap.entrySet()) {
            String word = entry.getKey();
            lastResult.put(word, lastResult.getOrDefault(word, 0) + entry.getValue());
        }
    }

    private static class work implements Callable {
        private BufferedReader br;

         work(BufferedReader br) {
            this.br = br;
        }


        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
