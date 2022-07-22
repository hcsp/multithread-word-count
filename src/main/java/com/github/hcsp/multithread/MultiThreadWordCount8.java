package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class MultiThreadWordCount8 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        /**
         * 安全:
         * 协同:
         * ForkJoinPool => invoke & ForkJoinTask
         */
        return new ForkJoinPool(threadNum).submit(new WordCounter8(files)).get();
    }
}
