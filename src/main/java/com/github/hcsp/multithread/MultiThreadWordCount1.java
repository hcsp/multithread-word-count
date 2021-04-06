package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount1 {
    private static final Map<String, Integer> countRes = new HashMap<>();
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Object lock = new Object();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        AtomicInteger filesSize = new AtomicInteger(files.size());
        for (int i = 1; i <= threadNum; i++) {
            new Thread(() -> {
                int fileIndex;
                synchronized (lock) {
                    fileIndex = filesSize.decrementAndGet();
                }
                if (fileIndex >= 0) {
                    Path path = Paths.get(files.get(fileIndex).getPath());
                    List<String> lines = null;
                    try {
                        lines = Files.readAllLines(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert lines != null;
                    for (String line : lines) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            synchronized (lock) {
                                countRes.put(word, countRes.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                }
                if (fileIndex == 0) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            }).start();
        }
        if (filesSize.get() > 0) {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return countRes;
    }
}
