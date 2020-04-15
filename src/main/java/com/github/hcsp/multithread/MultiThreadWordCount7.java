package com.github.hcsp.multithread;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

/**
 * 使用 CyclicBarrier 实现
 */
public class MultiThreadWordCount7 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, BrokenBarrierException {
        CyclicBarrier count = new CyclicBarrier(files.size() + 1);
        List<Map<String, Integer>> result = new CopyOnWriteArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                try {
                    result.add(Util.countWordFromOneFile(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    count.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        count.await();

        return Util.mergeMapsFromList(result);
    }
}
