package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount2 {
    //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        //最终结果
        Map<String, Integer> finalResultMap = new HashMap<>();

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Object lock = new Object();
            Map<String, Integer> resultMap = new HashMap<>();

            for (int i = 0; i < threadNum; i++) {
                new Thread(new WorkerJob(reader, lock, resultMap));
            }
            mergeResultIntoFinalResult(resultMap, finalResultMap);
        }

        return finalResultMap;
    }


    //工人做的事就是统计单词数量
    static class WorkerJob implements Runnable {
        private BufferedReader reader;
        private Object lock;
        private Map<String, Integer> resultMap;

        WorkerJob(BufferedReader reader, Object lock, Map<String, Integer> resultMap) {
            this.reader = reader;
            this.lock = lock;
            this.resultMap = resultMap;
        }

        @Override
        public void run() {
            synchronized (lock) {
                while (true) {
                    String line = null;
                    try {
                        if ((line = reader.readLine()) == null) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String[] words = line.split(" ");
                    for (String word : words) {
                        resultMap.getOrDefault(word, resultMap.put(word, 0) + 1);
                    }
                }
            }
        }
    }


    //把每行的结果合并起来
    private static void mergeResultIntoFinalResult(Map<String, Integer> resultMap,
                                                   Map<String, Integer> finalResultMap) {
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            String word = entry.getKey();
            int mergeResult = finalResultMap.getOrDefault(word, 0) + entry.getValue();
            finalResultMap.put(word, mergeResult);
        }
    }
}
