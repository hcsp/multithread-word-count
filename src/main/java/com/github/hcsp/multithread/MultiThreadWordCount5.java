package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Result result = new Result(new ConcurrentHashMap<>());

    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        /**
         * 安全: ConcurrentHashMap
         * 协同: Semaphore
         */
        int step = files.size() / threadNum;
        Semaphore emptySlot = new Semaphore(threadNum);
        Semaphore fullSlot = new Semaphore(0);

        for (int i = 0; i < threadNum; i++) {
            new WordCounter5(result, files.subList(i * step, i == threadNum - 1 ? files.size() : (i + 1) * step), emptySlot, fullSlot).start();
        }

        fullSlot.acquire();

        return result.value;
    }
}
