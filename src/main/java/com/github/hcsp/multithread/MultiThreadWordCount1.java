package com.github.hcsp.multithread;


import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    private static ExecutorService threadPool;
    private final int threadNum;

    public MultiThreadWordCount1(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        //开启若干个线程,每个线程去读取文件的一行内容,并将其中的单词统计结果返回
        //最后,主线程将工作线程返回的结果汇总在一起
        for (int i = 0; i < threadNum; ++i) {
            futures.add(
                    threadPool.submit(new WorkerJob(reader)));
        }
        List<Map<String, Integer>> resultFromWorkers = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);

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
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("");
                for (String word : words) {
                    if (result.containsKey(word)) {
                        result.put(word, result.get(word) + 1);
                    } else {
                        result.put(word, 1);
                    }
                }
            }
            return result;
        }
    }


}
