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

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        ArrayList<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<HashMap<String, Integer>> future = threadPool.submit(new Callable<HashMap<String, Integer>>() {
                @Override
                public HashMap<String, Integer> call() throws Exception {
                    return countSingleFileWord(file);
                }
            });
            futures.add(future);
        }
        HashMap<String, Integer> result = new HashMap<>();
        for (Future<HashMap<String, Integer>> future : futures) {
            HashMap<String, Integer> singleFileWordCount = future.get();
            for (String word :
                    singleFileWordCount.keySet()) {
                result.put(word, result.getOrDefault(word, 0) + singleFileWordCount.get(word));
            }
        }
        return result;
    }

    public static HashMap<String, Integer> countSingleFileWord(File file) throws IOException {
        HashMap<String, Integer> singleFileWordCount = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            for (String word : line.split(" ")) {
                singleFileWordCount.put(word, singleFileWordCount.getOrDefault(word, 0) + 1);
            }
        }
        return singleFileWordCount;
    }
}
