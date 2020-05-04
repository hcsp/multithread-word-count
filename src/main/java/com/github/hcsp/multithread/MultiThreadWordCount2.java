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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量 Lock/Condition

    public static void main(String[] args) throws IOException {
        List<File> files = Arrays.asList(new File("c:/users/sunp/git/multithread-word-count/target/test.txt"),
                new File("c:/users/sunp/git/multithread-word-count/target/test2.txt"));
        System.out.println(count(10, files));
    }

    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        final Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Map<String, Integer> result = new HashMap<>();
        int filesCount = 0;
        for (File file : files) {
            List<Map<String, Integer>> middleList = new ArrayList<>();
            BufferedReader bReader = Files.newBufferedReader(file.toPath());
            for (int i = 0; i < threadNum; i++) {
                Map<String, Integer> middleResult = new HashMap<>();
                new Thread(() -> {
                    lock.lock();
                    try {
                        String line;
                        while (null != (line = bReader.readLine())) {
                            String[] strings = line.split(" ");
                            condition.await();
                            for (String word : strings) {
                                middleResult.put(word, middleResult.getOrDefault(word, 0) + 1);
                            }
                            condition.signal();
                        }
                        middleList.add(middleResult);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        lock.unlock();
                    }
                }).start();
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
