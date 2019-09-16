package com.github.hcsp.multithread;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }


    // 统计文件中各单词的数量
    Map<String, Integer> count(List<File> files) {
        Map<String, Integer> finalResult = new HashMap<>();


        files.forEach(file -> {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException();
            }

            IntStream.rangeClosed(1, threadNum)
                    .mapToObj(x -> threadPool.submit(new WorkerJob(reader)))
                    .forEach(future -> {
                        try {
                            mergeWorkerResultIntoFinalResult(future.get(), finalResult);
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException();
                        }
                    });
        });



        return finalResult;
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

