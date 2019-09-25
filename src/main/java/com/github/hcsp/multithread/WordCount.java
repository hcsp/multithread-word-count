package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    public static void main(String[] args) {

    }
    // 统计所有文件中单词的数量
    public Map<String, Integer> count(List<File> files) {
        return getFinalResult(setCollectionFileFutures(files));
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return getFinalResult(setWorkerJobFutures(new WorkerJob(reader)));
    }

    private List<Future<Map<String, Integer>>> setCollectionFileFutures(List<File> files) {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            futures.add(threadPool.submit(new CollectionFileJob(files.get(i))));
        }
        return futures;
    }

    private List<Future<Map<String, Integer>>> setWorkerJobFutures(Callable<Map<String, Integer>> callable) {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        // 开多个线程，每个线程读取文件的一行内容，并将其中的单词统计结果返回
        // 最后，主线程将 工作线程返回的结果汇总在一起
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(callable));
        }
        return futures;
    }

    private Map<String, Integer> getFinalResult(List<Future<Map<String, Integer>>> futures) {
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = null;
            try {
                resultFromWorker = future.get();
                mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return finalResult;
    }

    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                  Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line = null;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    class CollectionFileJob implements Callable<Map<String, Integer>> {
        private File file;

        CollectionFileJob(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            return count(file);
        }
    }
}


