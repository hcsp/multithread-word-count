package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    //使用threadNum个线程，并发统计文件中各单词的数量

    private final int threadNum;
    private static ExecutorService threadPool;

    public MultiThreadWordCount2(int threadNum) {
        this.threadNum = threadNum;
        threadPool = Executors.newFixedThreadPool(threadNum);

    }

    public Map<String, Integer> count(List<File> files) throws ExecutionException, InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        // 遍历所有文件，每个文件用线程池中的线程去读取
        for (File file : files) {
            Map<String, Integer> result = new HashMap<>();
            futures.add(threadPool.submit(() -> {
                Scanner fileScanner = new Scanner(file);
                for (int i = 0; i < threadNum; i++) {
                    MultiThreadReadLine readLine = new MultiThreadReadLine(result, fileScanner);
                    threadPool.submit(readLine);
                }
                return result;
            }));
        }

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> count = future.get();
            mergeCountIntoFinalResult(count, finalResult);
        }

        return finalResult;
    }

    private void mergeCountIntoFinalResult(Map<String, Integer> count, Map<String, Integer> finalResult) {
        for (
                Map.Entry<String, Integer> entry : count.entrySet()
        ) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }


    // 多线程逐行读取一个文件
    static class MultiThreadReadLine implements Callable<Map<String, Integer>> {
        String line;
        Map<String, Integer> result;
        Scanner fileScanner;

        MultiThreadReadLine(Map<String, Integer> result, Scanner fileScanner) {
            this.result = result;
            this.fileScanner = fileScanner;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            while ((line = fileScanner.nextLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

}
