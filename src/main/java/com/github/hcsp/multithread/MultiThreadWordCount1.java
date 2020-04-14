package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(threadPool.submit(new WorkerJob(file)));
        }

        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            mergeWorkerResultToFinalResult(finalResult, future.get());
        }

        threadPool.shutdown();
        return finalResult;

    }

    private static class WorkerJob implements Callable<Map<String, Integer>> {
        File file;

        WorkerJob(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Map<String, Integer> wordMap = new HashMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);
                }
            }
            return wordMap;
        }
    }


    private static void mergeWorkerResultToFinalResult(Map<String, Integer> finalResult,
                                                       Map<String, Integer> resultFromWork) {
        Set<String> keys = resultFromWork.keySet();
        for (String key : keys) {
            finalResult.put(key, finalResult.getOrDefault(key, 0) + resultFromWork.get(key));
        }
//        for (Map.Entry<String, Integer> entry : resultFromWork.entrySet()) {
//            String word = entry.getKey();
//            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
//            finalResult.put(word, mergedResult);
//        }
    }

//    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
//        List<File> files = new ArrayList<>();
//        for (int i = 1; i < 3; i++) {
//            File file = new File("C:\\Users\\Dandan\\IdeaProjects\\multithread-word-count\\" + i + ".txt");
//            files.add(file);
//        }
//        MultiThreadWordCount1 multiThreadWordCount1 = new MultiThreadWordCount1(2);
//        Map<String, Integer> results = multiThreadWordCount1.count(2, files);
//        System.out.println(results);
//        threadPool.shutdown();
//    }


}
