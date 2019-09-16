package com.github.hcsp.multithread;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class WordCount {
    private final int threadNum;
    private final ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }


    // 统计文件中各单词的数量
    Map<String, Integer> count(List<File> files) throws RuntimeException, ExecutionException, InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (File file : files) {
            BufferedReader reader = createBufferedReader(file);

            // 将reader丢进一个worker处理，同时启用多个线程跑多个worker
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new WorkerJob(reader)));
            }

        }

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }

        return finalResult;
    }


    private BufferedReader createBufferedReader(File file) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        }
        return reader;
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
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                  Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            Integer wordCount = entry.getValue();
            int mergedResult = finalResult.getOrDefault(word, 0) + wordCount;
            finalResult.put(word, mergedResult);
        }
    }
}

