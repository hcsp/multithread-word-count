package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// CountDownLatch
public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> result = new HashMap<>();
        List<WorkJob> workList = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        CountDownLatch doneSignal = new CountDownLatch(threadNum);
        for (File file : files) {
            WorkJob work = new WorkJob(file, doneSignal, result);
            threadPool.submit(work);
            workList.add(work);
        }
        doneSignal.await();
        for (WorkJob work : workList) {
            FileUtil.merge(work.getResult(), result);
        }
        return result;
    }

    private static class WorkJob implements Runnable {
        private File file;
        private CountDownLatch doneSignal;
        private Map<String, Integer> result;

        Map<String, Integer> getResult() {
            return result;
        }

        WorkJob(File file, CountDownLatch doneSignal, Map<String, Integer> result) {
            this.doneSignal = doneSignal;
            this.file = file;
            this.result = result;
        }

        @Override
        public void run() {
            result = FileUtil.work(file);
            doneSignal.countDown();
        }
    }
}
