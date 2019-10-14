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
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        //用固定线程池
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) throws FileNotFoundException, ExecutionException, InterruptedException {
        //开若干个线程，每个线程去读取文件的一行内容，并将其中的单词统计结果返回
        //最后，主线程将工作线程返回的结果汇总在一起
        //拿到若干的结果，要用一个future去存储它
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File files : file){
            BufferedReader reader = new BufferedReader(new FileReader(files));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new WorkerJob(reader)));
            }

        }


        //收集最后的数据
        Map<String, Integer> finalResult = new HashMap<>();
        //收集数据
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }

        return finalResult;

    }


    private void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();

            int mergeResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergeResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        //通过构造器吧reader传入进来
        private BufferedReader reader;

        WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            HashMap<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    //Java8后引用的方法
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}

























