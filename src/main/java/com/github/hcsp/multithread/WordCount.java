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
    private int  threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file:files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i=0; i<threadNum; i++) {
                futures.add(threadPool.submit(new WorkerJob(reader)));
            }
        }

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            merWorkerResultIntoFinalResult(resultFromWorker, finalResult);

        }
        return finalResult;
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {

        private BufferedReader reader;

        WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        Map<String, Integer> result = new HashMap<>();

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            while ((line=reader.readLine())!=null) {
                String[] words = line.split(" ");

                for (String word : words){
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    private void merWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();

            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }
}
