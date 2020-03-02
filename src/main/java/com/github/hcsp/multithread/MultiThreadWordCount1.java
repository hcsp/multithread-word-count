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

public class MultiThreadWordCount1 {


    //开若干线程，每个线程读取文件的一行内容（且假设文件中是以" "分行），并将其中的单词统计结果返回
    //最后，主线程将工作线程的返回的结果汇总
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        // 使用threadNum个线程并发统计文件中各单词的数量
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new WorkerJob(reader)));
            }
        }


        Map<String, Integer> finalResult = new HashMap<>();

        for (Future<Map<String, Integer>> future : futures) {
            //每个线程的结果拿到
            Map<String, Integer> resultFromWorkers = future.get();
            //汇总每个线程的结果
            mergeWorkerResultIntoFinalResult(resultFromWorkers, finalResult);
        }
        return finalResult;
    }


    //合并每个worker的工作
    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorkers,
                                                         Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorkers.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }


    //声明一个内部类,每个工人要做的事（遍历一行，统计单词个数），并返回结果
    static class WorkerJob implements Callable<Map<String, Integer>> {

        private BufferedReader bufferedReader;

        WorkerJob(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1); //下面四行在Java8之后可简化成此行
                          /*  if(result.containsKey(word)){
                                result.put(word,result.get(word)+1);
                            }else {
                                result.put(word,1);
                            }*/
                }
            }
            return result;
        }
    }
}
