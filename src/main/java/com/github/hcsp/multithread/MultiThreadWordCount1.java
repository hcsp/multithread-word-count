package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount1 {
    private static int fileIndex = 0;
    private static HashMap<String, Integer> result = new HashMap<>();
    private static final Object lock = new Object();
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    getTheCountResult(files);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        return result;
    }

    private static void getTheCountResult(List<File> files) throws IOException {
        synchronized (lock) {
            while (files.size() > fileIndex) {
                File file = files.get(fileIndex);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }
                fileIndex++;
            }
        }

    }
}
