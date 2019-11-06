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

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws ExecutionException, InterruptedException, FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        // 开若干个线程, 每个线程去读取文件的一行内容,并将其中的单词统计结果返回
        // 最后, 主线程将工作线程返回的结果汇总在一起
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new WorkerJob(reader)));
        }
        List<Map<String, Integer>> resultsFromWorkers = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultsFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultsFromWorker, finalResult);
        }
        return finalResult;
    }

    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultsFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultsFromWorker.entrySet()) {
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
        public Map<String, Integer> call() throws IOException {
            String line;
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
}
