package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<String, Integer> count(File file) throws IOException, ExecutionException, InterruptedException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> list = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        for (int i = 0; i < threadNum; ++i) {
            list.add(threadPool.submit(() -> {
                Map<String, Integer> result = new HashMap<>();
                String line;
                while ((line = bufferedReader.readLine())!=null) {
                    String[] oneThreadReadOneLine = line.split(" ");
                    for (String element : oneThreadReadOneLine
                    ) {
                        result.put(element, result.getOrDefault(element, 0)+1);
                    }
                }
                return result;
            }));
        }

        for (Future<Map<String, Integer>> future : list
        ) {
            Map<String, Integer> resultFromThread = future.get();
            mergeResultFromThread(resultFromThread, finalResult);
        }
        return finalResult;
    }

    private void mergeResultFromThread(Map<String, Integer> resultFromThread,
                                       Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromThread.entrySet()) {
            int resultNumber = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), resultNumber);
        }
    }
}

