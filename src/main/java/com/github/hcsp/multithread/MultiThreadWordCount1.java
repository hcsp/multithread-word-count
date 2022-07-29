package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> finalResult = new HashMap<>();

        for (File file :
                files) {
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();
            BufferedReader buf = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new MySubmit(buf)));
            }

            for (Future<Map<String, Integer>> future :
                    futures) {
                Map<String, Integer> resultFromSubmit = future.get();
                mergeSubmitResultIntoFinalResult(resultFromSubmit, finalResult);
            }
        }
        return finalResult;
    }

    private static void mergeSubmitResultIntoFinalResult(Map<String, Integer> resultFromSubmit, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry :
                resultFromSubmit.entrySet()) {
            String word = entry.getKey();
            finalResult.put(word, finalResult.getOrDefault(word, 0) + entry.getValue());
        }
    }

    static class MySubmit implements Callable {
        private final BufferedReader buf;

        MySubmit(BufferedReader buf) {
            this.buf = buf;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> result = new HashMap<>();
            String line;
            while ((line = buf.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word :
                        words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
