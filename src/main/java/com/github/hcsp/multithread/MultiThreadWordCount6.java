package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Object lock = new Object();
    private static final Map<String, Integer> resultMap = new ConcurrentHashMap<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        files.parallelStream().forEach(file -> {
            synchronized (lock) {
                ProcessFile.convertWordsInFileToMap(file, resultMap);
            }
        });

        return resultMap;
    }
}

