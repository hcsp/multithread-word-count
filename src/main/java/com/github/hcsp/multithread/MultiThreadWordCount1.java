package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount1 {
    static CountDownLatch latch = null;
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> wordCountMapList = new ArrayList<>();
        latch = new CountDownLatch(threadNum * files.size());
        for (File file : files) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("file not found" + file);
            }

            for (int i = 0; i < threadNum; i++) {
                new Thread(new ReadFileWithCountLatch(reader, latch, wordCountMapList)).start();
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Map<String, Integer> wordCountMap : wordCountMapList) {
            for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {

                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return result;
    }

}
