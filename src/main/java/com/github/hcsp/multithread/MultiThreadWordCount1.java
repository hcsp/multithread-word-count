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

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        File file = new File("C:\\Users\\25224\\Desktop\\text.txt.txt");
        MultiThreadWordCount1 count2 = new MultiThreadWordCount1(10);
        System.out.println(count2.count(10, file));

    }

    public Map<String, Integer> count(int threadNum, File file) throws FileNotFoundException, ExecutionException, InterruptedException {

        Map<String, Integer> finalResult = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    String line = null;
                    Map<String, Integer> result = new HashMap<>();
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split("");
                        for (String word : words) {
                            if (result.containsKey(word)) {
                                result.put(word, result.getOrDefault(word, 0) + 1);
                            }
                        }

                    }
                    return result;
                }
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
