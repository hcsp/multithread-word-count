package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> mapList = new ArrayList<>();

        List<ReadFileWithThread> threads = new ArrayList<>();
        for (File file : files) {
            for (int i = 0; i < threadNum; i++) {
                ReadFileWithThread readFileThread = new ReadFileWithThread(file);
                threads.add(readFileThread);
                readFileThread.start();
            }
        }

        for (ReadFileWithThread thread : threads) {
            try {
                thread.join();
                mapList.add(thread.getMap());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mapAdd(result, mapList);
        return result;
    }

    private static void mapAdd(Map<String, Integer> result, List<Map<String, Integer>> wordCountMapList) {
        for (Map<String, Integer> wordCountMap : wordCountMapList) {
            for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {

                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(count(10, new ReadFileUtils().createTestFiles()));
    }
}
