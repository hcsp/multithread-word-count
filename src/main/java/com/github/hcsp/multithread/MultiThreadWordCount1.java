package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        HashMap<String, Integer> finalResult = new HashMap<>(100);
        List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            Future<HashMap<String, Integer>> future = threadPool.submit(() -> countFile(file));
            futures.add(future);
        }

        for (Future<HashMap<String, Integer>> future : futures) {
            mergeToFinalResult(finalResult, future.get());
        }

        return finalResult;
    }

    private static void mergeToFinalResult(HashMap<String, Integer> finalResult, HashMap<String, Integer> hashMap) {
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            int result = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), result);
        }
    }

    private static HashMap<String, Integer> countFile(File file) throws IOException {
        HashMap<String, Integer> result = new HashMap<>(110);

        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        ) {
            while ((line = bufferedReader.readLine()) != null) {
                String[] strings = line.split(" ");
                for (String word : strings) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
        }

        return result;
    }

}
