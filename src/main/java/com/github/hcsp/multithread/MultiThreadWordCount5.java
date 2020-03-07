package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount5 {
    private static ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 分组files处理
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        List<Thread> threads = new ArrayList<>();
        List<List<File>> group = groupFiles(files, threadNum);
        group.forEach(item -> {
            threads.add(new Thread(() -> ReaderUtils.readFilesToMap(item, concurrentHashMap)));
        });
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return concurrentHashMap;
    }

    private static List<List<File>> groupFiles(List<File> files, int threadNum) {
        List<List<File>> group = new ArrayList<>();
        int capacity = files.size() / threadNum == 0 ? 1 : files.size() / threadNum;
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            fileList.add(files.get(i));
            if ((i + 1) % capacity == 0) {
                group.add(fileList);
                fileList.clear();
            }
        }
        return group;
    }
}
