package com.github.hcsp.multithread;


import com.google.common.collect.Lists;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量

    static Lock lock = new ReentrantLock();

    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException {
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();
        List<List<File>> fileGroup = Lists.partition(files, threadNum);
        AtomicInteger fileCount = new AtomicInteger(files.size());
        List<Map<String, Integer>> wordCountList = new ArrayList<>();
        for (List<File> fileList : fileGroup) {
            new Thread(() -> {
                for (File file : fileList) {
                    wordCountList.add(new WordCountTask().task(file, fileCount));
                }
            }).start();

        }
        try {
            while (fileCount.get() > 0) {
                lock.lock();
            }
        } finally {
            lock.unlock();
        }
        for (Map<String, Integer> resultFromWorker : wordCountList) {
            new MergeWorker().mergeWorkerResultIntoFinalResult(resultFromWorker, wordCount);
        }
        return wordCount;
    }
}
