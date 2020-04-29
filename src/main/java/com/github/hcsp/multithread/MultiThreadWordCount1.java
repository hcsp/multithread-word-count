package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        //开若干线程，每个线程去读取文件的一行内容，并将其中的单词统计结果返回；
        //最后，主线程将工作线程返回的结果汇总在一起
        //创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        //将返回的Future收集起来
        Map<String, Integer> finalResult = new HashMap<>();

        //循环获取文件内容
        for (int i = 0; i < files.size(); i++) {
            BufferedReader reader = new BufferedReader(new FileReader(files.get(i)));

            //创建Future的map集合
            List<Future<Map<String, Integer>>> futures = new ArrayList();

            //循环向线程池提交任务
            for (int j = 0; j < threadNum; ++j) {
                //Runnable没有返回值，使用Callable，返回值是Future
                futures.add(threadPool.submit(new WorkerJob(reader)));
            }

            //循环遍历futures，将结果收集起来放入finalResult
            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> resultFromWorker = future.get();
                //开启一个方法来将收集的结果
                mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
            }
        }
        return finalResult;
    }


    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                         Map<String, Integer> finalResult) {
        //使用map的Entry来遍历
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();

            int mergeResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergeResult);
        }
    }

    //将任务抽取出一个方法
    static class WorkerJob implements Callable<Map<String, Integer>> {
        public BufferedReader reader;

        WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            //创建HashMap存放统计的值
            Map<String, Integer> result = new HashMap<>();
            String line = null;
            while ((reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    //想map添加数据
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}

