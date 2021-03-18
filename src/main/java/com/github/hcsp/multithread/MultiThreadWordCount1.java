package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
                            addWord(word);
                        }
                    }
                    if(fileIndex == 0)
                    {
                        lock.notify();
                    }
                }
            });
        }
        if(filesSize.get() >= 0)
        {

        }
        return countRes;
    }


    public synchronized static void addWord(String word) {
        countRes.put(word, countRes.getOrDefault(word, 0) + 1);
    }

}
