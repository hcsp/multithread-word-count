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
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;

    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResults = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkResultIntoFinalResult(resultFromWorker, finalResults);


        }
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    String line;
                    Map<String, Integer> result = new HashMap<>();
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            result.put(word, result.getOrDefault(word, 0) + 1);
//                              if (result.containsKey(word)){
//                                  result.put(word,result.get(word)+1);
//
//                              }
//                              else {
//                                  result.put(word,1);
//                              }
                        }

                    }

                    return result;
                }
            }));


        }
        return finalResults;
    }

    private void mergeWorkResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResults) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResults.getOrDefault(word, 0) + entry.getValue();
            finalResults.put(word, mergedResult);
        }
    }
}
