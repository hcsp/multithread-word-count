package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量 Lock/Condition

    public static void main(String[] args) throws IOException, InterruptedException {
        List<File> files = Arrays.asList(new File("c:/users/sunp/git/multithread-word-count/target/test3.txt"),
                new File("c:/users/sunp/git/multithread-word-count/target/test4.txt"));
        System.out.println(count(10, files));
    }

    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, InterruptedException {
        final Object lock = new Object();
        int count = 0;
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> middleList = new ArrayList<>();
        for (File file : files) {
            int newCount = count;
            BufferedReader bReader = Files.newBufferedReader(file.toPath());
            for (int i = 0; i < threadNum; i++) {
                Map<String, Integer> middleResult = new HashMap<>();
                Thread thread = new Thread(() -> {
                    try {
                        String line;
                        while (null != (line = bReader.readLine())) {
                            String[] strings = line.split(" ");
                            for (String word : strings) {
                                middleResult.put(word, middleResult.getOrDefault(word, 0) + 1);
                            }
                        }
                        middleList.add(middleResult);
                        synchronized (lock) {
                            if (newCount == files.size()) {
                                lock.notify();
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();            
            }
            count++;
        }
        synchronized (lock) {
            if (count != files.size()) {
                lock.wait();
            }
            for (Map<String, Integer> stringIntegerMap : middleList) {
                stringIntegerMap.forEach((key, value) -> {
                    result.put(key, result.getOrDefault(key, 0) + value);
                });
            }
        }
        return result;
    }
}
