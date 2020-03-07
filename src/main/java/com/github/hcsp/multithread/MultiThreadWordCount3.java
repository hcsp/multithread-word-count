package com.github.hcsp.multithread;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    private static ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
    private static final Object obj = new Object();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    // CountDownLatch实现
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, FileNotFoundException {
        for (File file : files) {
            CountDownLatch count = new CountDownLatch(threadNum);
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                new Worker(br, count).start();
            }
            count.await();
        }
        return concurrentHashMap;
    }

    static class Worker extends Thread {
        private BufferedReader bf;
        private CountDownLatch count;

        public Worker(BufferedReader bf, CountDownLatch count) {
            this.bf = bf;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                ReaderUtils.readFileToConcurrencyMap(bf, concurrentHashMap);
                count.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
