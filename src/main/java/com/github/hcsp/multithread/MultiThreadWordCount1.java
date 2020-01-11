
package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {

    // 每个文件开一个线程，统计词频
    public static HashMap<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(threadPool.submit(new WordsCountInFile(file)));
        }
        return resultMerge(futures);
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

    static class WordsCountInFile implements Callable<HashMap<String, Integer>> {
        File file;

        WordsCountInFile(File file) {
            this.file = file;
        }

        @Override
        public HashMap<String, Integer> call() throws FileNotFoundException {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            HashMap<String, Integer> wordsCountInLine = new HashMap<>();
            try {
                String stringInLine;
                while ((stringInLine = fileReader.readLine()) != null) {
                    String[] wordsInLine = stringInLine.split(" ");
                    for (String string : wordsInLine) {
                        wordsCountInLine.put(string, wordsCountInLine.getOrDefault(string, 0) + 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return wordsCountInLine;
        }
    }
}

