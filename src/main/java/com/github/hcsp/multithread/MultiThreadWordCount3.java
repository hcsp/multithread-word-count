package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量 Lock/Condition
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        final Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Map<String, Integer> result = new HashMap<>();
        for (File file : files) {
            BufferedReader bReader = Files.newBufferedReader(file.toPath());
            for (int i = 0; i < threadNum; i++) {
                new Thread(() -> {
                    lock.lock();
                    try {
                        String line = bReader.readLine();
                        while (null != line) {
                            String[] strings = line.split(" ");
                            condition.await();
                            for (String word : strings) {
                                result.put(word, result.getOrDefault(word, 0) + 1);
                            }
                            condition.signal();
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        lock.unlock();
                    }
                }).start();
            }
        }
        return result;
    }
}
