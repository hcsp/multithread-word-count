package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用CyclicBarrier

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);
        List<Map<String, Integer>> mapList = new CopyOnWriteArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> oneMap = CountUtil.countOneFile(file);
                mapList.add(oneMap);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

            }).start();

        }
        return CountUtil.mergeFileResult(mapList);
    }


}
