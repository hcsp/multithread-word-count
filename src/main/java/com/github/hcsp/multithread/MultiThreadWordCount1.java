package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadWordCount1 {
    private static ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用join方式，每一个线程一个线程去读取一个文件
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, FileNotFoundException {
        for (File file : files) {
            List<Thread> threads = new ArrayList<>();
            BufferedReader bf = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                Thread worker = new Worker(bf, concurrentHashMap);
                threads.add(worker);
            }
            threads.forEach(thread -> {
                try {
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        return concurrentHashMap;
    }

    static class Worker extends Thread {
        private BufferedReader bf;
        private ConcurrentHashMap<String, Integer> concurrentHashMap;

        public Worker(BufferedReader bf, ConcurrentHashMap<String, Integer> concurrentHashMap) {
            this.bf = bf;
            this.concurrentHashMap = concurrentHashMap;
        }

        @Override
        public void run() {
            try {
                ReaderUtils.readFileToConcurrencyMap(bf, concurrentHashMap);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
