
package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {

    // 使用多线程对单个文件中的每一行进行处理。然后汇总所有文件的统计结果
    public static HashMap<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException, IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            futures = wordsCountSingleFile(file, threadPool, futures);
        }
        return resultMerge(futures);
    }

    public static List<Future<HashMap<String, Integer>>> wordsCountSingleFile(File file, ExecutorService threadPool, List<Future<HashMap<String, Integer>>> futures) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String stringInLine;
        while ((stringInLine = fileReader.readLine()) != null) {
            futures.add(threadPool.submit(new CountSingleLineUsingMultiThread(stringInLine)));
        }
        return futures;
    }

    public static HashMap<String, Integer> resultMerge(List<Future<HashMap<String, Integer>>> futures) throws ExecutionException, InterruptedException {
        HashMap<String, Integer> countResult = new HashMap<>();
        for (Future<HashMap<String, Integer>> future : futures) {
            resultMergeHelper(future.get(), countResult);
        }
        return countResult;
    }

    private static HashMap<String, Integer> resultMergeHelper(HashMap<String, Integer> futureGet, HashMap<String, Integer> countResult) {
        for (String key : futureGet.keySet()) {
            countResult.put(key, (countResult.getOrDefault(key, 0) + futureGet.get(key)));
        }
        return countResult;
    }

    static class CountSingleLineUsingMultiThread implements Callable<HashMap<String, Integer>> {
        String stringInLine;

        CountSingleLineUsingMultiThread(String stringInLine) {
            this.stringInLine = stringInLine;
        }

        @Override
        public HashMap<String, Integer> call() {
            HashMap<String, Integer> wordsCountInLine = new HashMap<>();
            String[] wordsInLine = stringInLine.split(" ");
            for (String string : wordsInLine) {
                wordsCountInLine.put(string, wordsCountInLine.getOrDefault(string, 0) + 1);
            }
            return wordsCountInLine;
        }
    }
}

