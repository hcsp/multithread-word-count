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
    // 使用线程池
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> resultsFuture = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Future<Map<String, Integer>> mapFuture = threadPool.submit(new readFileCallable(bufferedReader));
            resultsFuture.add(mapFuture);
        }
        threadPool.shutdown();
        return mergeCountResults(resultsFuture);
    }

    private static Map<String, Integer> mergeCountResults(List<Future<Map<String, Integer>>> resultsFuture) throws ExecutionException, InterruptedException {
        Map<String, Integer> finalResults = new HashMap<>();
        for (Future<Map<String, Integer>> mapFuture : resultsFuture) {
            Map<String, Integer> stringIntegerMap = mapFuture.get();
            for (Map.Entry<String, Integer> stringIntegerEntry : stringIntegerMap.entrySet()) {
                String word = stringIntegerEntry.getKey();
                int updatedValue = finalResults.getOrDefault(word, 0) + stringIntegerEntry.getValue();
                finalResults.put(word, updatedValue);
            }
        }
        return finalResults;
    }

    public static class readFileCallable implements Callable<Map<String, Integer>> {
        BufferedReader bufferedReader;

        public readFileCallable(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> fileWords = new HashMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    fileWords.put(word, fileWords.getOrDefault(word, 0) + 1);
                }
            }
            return fileWords;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        List<File> files = new ArrayList<>();
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\1.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\2.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\3.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\4.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\5.txt"));
        files.add(new File("D:\\project\\multithread-word-count\\src\\main\\java\\com\\github\\hcsp\\multithread\\6.txt"));
        Map<String, Integer> results = MultiThreadWordCount1.count(5, files);
        for (Map.Entry<String, Integer> resultEntry : results.entrySet()) {
            System.out.println("Key: " + resultEntry.getKey()+"    Value: " + resultEntry.getValue());
        }
    }
}

