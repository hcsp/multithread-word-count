package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Map<String,Integer>> finalResultList = new ArrayList<>();
        Map<String,Integer> finalResult = new HashMap<>();
        for (File file : files) {

            List<Future<Map<String, Integer>>> futures = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new WorkerJob(reader)));
            }
            Map<String,Integer> tempResult = new HashMap<>();
            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> resultFromWorker = future.get();
                mergeWorkerResultIntoFinalResult(resultFromWorker,tempResult);
            }
            finalResultList.add(tempResult);
        }
        for (Map<String, Integer> stringIntegerMap : finalResultList) {
            mergeWorkerResultIntoFinalResult(stringIntegerMap,finalResult);
        }
        return finalResult;
    }

    private static Map<String, Integer> mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                                         Map<String, Integer> finalResult) {
        for (Map.Entry<String,Integer> entry : resultFromWorker.entrySet()){
             String word = entry.getKey();
             int merge = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word,merge);
        }


        return finalResult;
    }

    static class  WorkerJob implements Callable <Map<String, Integer>>{
        BufferedReader reader;
        public WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }
        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> result = new HashMap<>();
            String line = null;
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



