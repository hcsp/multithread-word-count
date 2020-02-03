package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import static com.github.hcsp.multithread.Utils.countFile;
import static com.github.hcsp.multithread.Utils.mergeIntoFirstMap;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, BrokenBarrierException {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(files.size() + 1);
        for (File file : files) {
            new Thread(() -> {
                try {
                    Map<String, Integer> countResult = countFile(file);
                    synchronized (result) {
                        mergeIntoFirstMap(result, countResult); //这里的map全是concurrentHashMap
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        cyclicBarrier.await();

        return result;
    }
}
