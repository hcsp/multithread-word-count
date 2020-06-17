package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MultiThreadWordCount1 {
    //     使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        Map<String, Integer> map = new HashMap<>();
        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            Object lock = new Object();
            Map<String, Integer> oneFile = new HashMap<>();
            for (int i = 0; i < threadNum; i++) {
                new Thread(() -> {
                    synchronized (lock) {
                        while (true) {
                            String line = null;
                            try {
                                if ((bufferedReader.readLine()) == null) {
                                    break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String[] words = line.split(" ");

                            for (String word : words) {
                                oneFile.put(word, oneFile.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                });
            }
            mergeWorkerResultIntoFinalResult(oneFile, map);
        }
        return map;
    }

    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> oneFile, Map<String, Integer> resultMap) {
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            String word = entry.getKey();
            int mergedResult = oneFile.getOrDefault(word, 0) + entry.getValue();
            oneFile.put(word, mergedResult);
        }
    }
}
