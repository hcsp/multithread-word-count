package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws Exception {
//        开若干个线程 每个线程去读取文件的一行内容 并将其中的单词统计结果返回
//        最后主线程统计最终结果
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < threadNum; i++) {

            futures.add(threadPool.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    String line = null;
                    Map<String, Integer> result = new HashMap<>();

                    while ((line = reader.readLine()) != null) {

                        String[] words = line.split(" ");
                        for (String word :
                                words) {
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }

                    }

                    return result;
                }
            }));

        }

        Map<String, Integer> finalResult = new HashMap<>();

        for (Future<Map<String, Integer>> future :
                futures) {
            Map<String, Integer> resultFromWorker = future.get();

            mergeWorkerResultInfoFinalResult(resultFromWorker, finalResult);
        }

        return finalResult;
    }

    private void mergeWorkerResultInfoFinalResult(Map<String, Integer> resultFromWorker,
                                                  Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry :
                resultFromWorker.entrySet()) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
    }
}
