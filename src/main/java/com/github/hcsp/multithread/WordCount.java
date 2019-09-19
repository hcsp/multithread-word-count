package com.github.hcsp.multithread;

import java.io.*;
import java.nio.ReadOnlyBufferException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {
    private int  threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        Map<String, Integer> result = new HashMap<>();
        List<Future<Map<String, Integer>>> futrues = new ArrayList<>();

        for (File file:files){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            for (int i = 0; i < threadNum; i++) {
                futrues.add(threadPool.submit(() -> {
                    while((line = reader.readLine())!=null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }
                    }
                    return result;
                }));
            }
        }
        Map<String, Integer> finalResult = new HashMap<>();

        for(Future<Map<String, Integer>> future : futrues) {
            Map<String, Integer> resultFromWorker = future.get();
            merWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        return finalResult;
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
