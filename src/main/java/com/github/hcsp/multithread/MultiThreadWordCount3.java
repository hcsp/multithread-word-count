package com.github.hcsp.multithread;


import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch
 */
public class MultiThreadWordCount3 {


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> freqMap = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(files.size());
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Work> workList = new ArrayList<>();
        for (File file : files) {
            Work work = new Work(latch, file);
            executorService.submit(work);
            workList.add(work);
        }
        latch.await();
        for (Work work : workList) {
            FileUtil.merge(work.getFreqMap(), freqMap);
        }
        return freqMap;
    }

    static class Work implements Runnable {

        Map<String, Integer> freqMap;
        CountDownLatch latch;
        File file;

        Map<String, Integer> getFreqMap() {
            return freqMap;
        }

        Work(CountDownLatch latch, File file) {
            this.latch = latch;
            this.file = file;
            this.freqMap = new HashMap<>();
        }

        @Override
        public void run() {
            freqMap =FileUtil.count(file);
            latch.countDown();
        }
    }
}
