package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        return files.parallelStream()
                .map(MultiThreadWordCount4::getWorkerResult)
                .reduce(MultiThreadWordCount4::mergeWorkerResult).get();
    }

    public static Map<String, Integer> getWorkerResult(File file) {
        Map<String, Integer> result = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        while (true) {
            try {
                if ((line = reader.readLine()) == null){
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] words = line.split(" ");
            for (String word : words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
        return result;
    }

    public static Map<String, Integer> mergeWorkerResult(Map<String, Integer> result, Map<String, Integer> workerResult) {
        for (Map.Entry<String, Integer> entry : workerResult.entrySet()) {
            String word = entry.getKey();
            int mergedCount = result.getOrDefault(word, 0) + entry.getValue();
            result.put(word, mergedCount);
        }

        return result;
    }

}
