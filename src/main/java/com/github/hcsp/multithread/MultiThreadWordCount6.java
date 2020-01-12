package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        // 使用许可数量为1 的信号量作为 互斥锁
        Semaphore lock = new Semaphore(1);
        // 创建 闭锁用于 所有文件被处理后 主线程 执行合并操作
        CountDownLatch latch = new CountDownLatch(files.size());

        List<Map<String, Integer>> mapList = new ArrayList<>();
        files.forEach(file -> {
            new Thread(() -> {
                try {
                    lock.acquire();
                    mapList.add(MultiThreadWordCount1.countOneFile(file));
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                    lock.release();
                }
            }).start();
        });
        latch.await();
        return MultiThreadWordCount1.merge(mapList);
    }
}
