package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Result result = new Result(new ConcurrentHashMap<>());

    public static Map<String, Integer> count(int threadNum, List<File> files) throws BrokenBarrierException, InterruptedException {
        /**
         * 安全: ConcurrentHashMap
         * 协同: CyclicBarrier
         */
        int step = files.size() / threadNum;
        CyclicBarrier barrier = new CyclicBarrier(threadNum + 1);

        for (int i = 0; i < threadNum; i++) {
            new WordCounter4(result, files.subList(i * step, i == threadNum - 1 ? files.size() : (i + 1) * step), barrier).start();
        }

        barrier.await();
        return result.value;
    }
}
