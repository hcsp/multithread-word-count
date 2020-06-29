package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount2 {
    private static ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            readOneFileAndCountWord(reader, threadNum);
        }
        return result;
    }

    private static void readOneFileAndCountWord(BufferedReader reader, int threadNum) {
        for (int i = 0; i < threadNum; i++) {
            CountWord countWord = new CountWord(reader, result);
            countWord.call();
        }
    }

    private static class CountWord implements Callable<Map<String, Integer>> {
        BufferedReader reader;
        Map<String, Integer> result;

        public CountWord(BufferedReader reader, ConcurrentHashMap<String, Integer> result) {
            this.reader = reader;
            this.result = result;
        }

        @Override
        public Map<String, Integer> call() {
            String line;
            while (true) {
                try {
                    if ((line = reader.readLine()) == null) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
