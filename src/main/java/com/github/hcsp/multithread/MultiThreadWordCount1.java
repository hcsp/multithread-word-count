package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount1 {
    public static final Object LOCK = new Object();
    public static Map<String, Integer> resultMap = new HashMap<>();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        for (int i = 0; i < threadNum; ++i) {
            int finalI = i;
            new Thread(() -> insertContentToMapFromFile(files.get(finalI), finalI == files.size() - 1)).start();
        }

        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return resultMap;
    }

    private static void insertContentToMapFromFile(File file, boolean isLast) {
        synchronized (LOCK) {
            new ProcessFile(resultMap, file).processFile();
            if (isLast) {
                LOCK.notify();
            }
        }
    }
}
