
package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public HashMap<String, Integer> count(int threadNum, List<File> files)  {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader fileReader = null;
            try {
                fileReader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            futures.addAll(wordsCountSingleFile(fileReader, threadPool, threadNum));
        }
        return resultMerge(futures);
    }

    // 使用多线程对单文件进行词频记录工作，并将结果添加至类静态变量 wordsCountResultList
    public static List<Future<HashMap<String, Integer>>> wordsCountSingleFile (BufferedReader fileReader, ExecutorService threadPool, int threadNum) {
        List<Future<HashMap<String, Integer>>> countResultListSingleFile = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            countResultListSingleFile.add(threadPool.submit(new wordsCountInFile(fileReader)));
        }
        return countResultListSingleFile;
    }

    public static HashMap<String, Integer> resultMerge(List<Future<HashMap<String, Integer>>> wordsCountResultInList) {
        for (Future<HashMap<String, Integer>> individualWordsCount : wordsCountResultInList) {
            try {
                return resultMergeHelper(individualWordsCount.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static HashMap<String, Integer> resultMergeHelper(HashMap<String, Integer> stringIntegerConcurrentHashMap) {
        HashMap<String, Integer> countResult = new HashMap<>();
        for (String key : stringIntegerConcurrentHashMap.keySet()) {
            countResult.put(key, stringIntegerConcurrentHashMap.getOrDefault(key, 0) + stringIntegerConcurrentHashMap.get(key));
        }
        return countResult;
    }

    static class wordsCountInFile implements Callable<HashMap<String, Integer>> {

        BufferedReader fileReader;
        wordsCountInFile(BufferedReader fileReader) {
            this.fileReader = fileReader;
        }

        @Override
        public HashMap<String, Integer> call() {
            HashMap<String, Integer> wordsCountInLine = new HashMap<>();
            try {
                String stringInLine;
                while ((stringInLine = fileReader.readLine()) != null) {
                    String[] wordsInLine = stringInLine.split(" ");
                    for (String string : wordsInLine) {
                        wordsCountInLine.put(string, wordsCountInLine.getOrDefault(string, 0) + 1);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            return wordsCountInLine;
        }
    }
}

