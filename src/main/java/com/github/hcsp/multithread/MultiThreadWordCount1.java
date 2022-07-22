package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount1 {

    private static final Result result = new Result(new HashMap<>());

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        /**
         * 安全: synchronized
         * 协同: wait/notify
         */
        int step = files.size() / threadNum;

        synchronized (result) {
            for (int i = 0; i < threadNum; i++) {
                new WordCounter1(result, files.subList(i * step, i == threadNum - 1 ? files.size() : (i + 1) * step), i == threadNum - 1).start();
            }

            // 此处需要等待所有的线程处理完成
            try {
                result.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return result.value;
    }
}

