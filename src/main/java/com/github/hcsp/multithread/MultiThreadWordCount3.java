package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier实现
 */
public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        CyclicBarrier barrier = new CyclicBarrier(threadNum + 1);
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> mapList = new CopyOnWriteArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> oneFileMap = Utils.countOneFile(file);
                mapList.add(oneFileMap);
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        result = Utils.mergeMapList(mapList);
        return result;
    }
}
