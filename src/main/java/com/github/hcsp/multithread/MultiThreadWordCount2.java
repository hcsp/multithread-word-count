package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
     //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        List<ForkJoinTask<Map<String, Integer>>> resultFromWorkers = new ArrayList<>();
        for (File file : files) {
            resultFromWorkers.add(forkJoinPool.submit(new WorkerJob(file)));
        }

        for (ForkJoinTask<Map<String, Integer>> result : resultFromWorkers) {
            mergeWorkerJobToFinalResult(finalResult, result.join());
        }

        forkJoinPool.shutdown();
        return finalResult;
    }

    private static void mergeWorkerJobToFinalResult(Map<String, Integer> finalResult, Map<String, Integer> resultFromWorker) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }

    private static class WorkerJob extends RecursiveTask<Map<String, Integer>> {
        File file;

        WorkerJob(File file) {
            this.file = file;
        }

        @Override
        protected Map<String, Integer> compute() {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                HashMap<String, Integer> wordMapOfEveryWorker = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        wordMapOfEveryWorker.put(word, wordMapOfEveryWorker.getOrDefault(word, 0) + 1);
                    }
                }
                return wordMapOfEveryWorker;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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
//
//
//    }
}
