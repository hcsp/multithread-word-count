package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    private final int threadNum;
    private ExecutorService threadPool;

    public MultiThreadWordCount1(int threadNum) {
        this.threadNum = threadNum;
        this.threadPool = Executors.newFixedThreadPool(threadNum);
    }

    public Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        Vector<InputStream> inputStreamVercotr = new Vector<>();
        for (File file : files) {
            InputStream inputStream = new FileInputStream(file);
            inputStreamVercotr.add(inputStream);
        }
        SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStreamVercotr.elements());
        Map<String, Integer> finalResult = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(sequenceInputStream));
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(() -> {
                String line = null;
                Map<String, Integer> result = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        {
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }
                    }
                }
                return result;
            }));
        }
        for (Future<Map<String, Integer>> map : futures) {
            Map<String, Integer> map1 = map.get();
            for (Map.Entry<String, Integer> entry : map1.entrySet()) {
                int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
                finalResult.put(entry.getKey(), mergedResult);
            }
        }
        return finalResult;
    }
}
