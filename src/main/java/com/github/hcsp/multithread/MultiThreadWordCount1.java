package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.hcsp.multithread.FileUtil.merge;

//synchronized Object.wait()/notify() 方法
public class MultiThreadWordCount1 {
    private static final Object lock = new Object();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> result = new HashMap<>();
        List<WorkJob> workList = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        AtomicInteger count = new AtomicInteger(threadNum);
        for (File file : files) {
            WorkJob work = new WorkJob(count, file);
            threadPool.submit(work);
            workList.add(work);
        }

        synchronized (lock) {
            if (count.get() > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (WorkJob work : workList) {
                merge(work.getResult(), result);
            }

        }
        return result;
    }

    static class WorkJob implements Runnable {
        private Map<String, Integer> result;
        private AtomicInteger count;
        private File file;

        Map<String, Integer> getResult() {
            return result;
        }

        WorkJob(AtomicInteger count, File file) {
            this.count = count;
            this.file = file;
        }

        @Override
        public void run() {
            synchronized (lock) {
                result = FileUtil.work(file);
                if (count.decrementAndGet() == 0) {
                    lock.notify();
                }
            }
        }
    }
}
