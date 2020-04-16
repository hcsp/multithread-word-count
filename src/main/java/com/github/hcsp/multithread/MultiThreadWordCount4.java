package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        List<WorkJob> workList = new ArrayList<>();
        Map<String, Integer> result = new HashMap<>();
        AtomicInteger count = new AtomicInteger(threadNum);
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            WorkJob work = new WorkJob(file, count);
            threadPool.submit(work);
            workList.add(work);
        }

        lock.lock();
        try {
            if (count.get() > 0) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        for (WorkJob work : workList) {
            ReadFileUtils.mergeMap(result, work.getResult());
        }
        return result;
    }


    private static class WorkJob implements Runnable {
        private File file;
        private Map<String, Integer> result;
        private AtomicInteger count;

        Map<String, Integer> getResult() {
            return result;
        }

        WorkJob(File file, AtomicInteger count) {
            this.file = file;
            this.result = new HashMap<>();
            this.count = count;
        }

        @Override
        public void run() {
            lock.lock();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                ReadFileUtils.readWordsToMap(result, reader);
                if (count.decrementAndGet() == 0) {
                    condition.signal();
                }
            } catch (IOException e){
                throw new RuntimeException(e.getMessage());
            }finally{
                lock.unlock();
            }

        }
    }
}
