package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

// CyclicBarrier
public class MultiThreadWordCount7 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, BrokenBarrierException {
        Map<String, Integer> resultMap = new ConcurrentHashMap<>();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(files.size() + 1);
        for (File file : files) {
            new Thread(() -> {
                try {
                    Map<String, Integer> map = Common.countOneFile(file);
                    synchronized (MultiThreadWordCount7.class) {
                        Common.mergeToMap(resultMap, map);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        cyclicBarrier.await();
        return resultMap;
    }
}
