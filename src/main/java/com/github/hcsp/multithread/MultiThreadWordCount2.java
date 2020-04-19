package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        final Object lock = new Object();
        Map<String, Integer> result = new HashMap<>();
        for (File file : files) {
            BufferedReader bReader = Files.newBufferedReader(file.toPath());
            for (int i = 0; i < threadNum; i++) {
                new Thread(() -> {
                    synchronized (lock) {
                        try {
                            String line = bReader.readLine();
                            while (null != line) {
                                String[] strings = line.split(" ");
                                lock.wait();
                                for (String word : strings) {
                                    result.put(word, result.getOrDefault(word, 0) + 1);
                                }
                                lock.notify();
                            }
                        } catch (IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        }
        return result;
    }
}
