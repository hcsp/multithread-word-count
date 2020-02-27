package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * {@link java.util.concurrent.locks.Lock} / {@link java.util.concurrent.locks.Condition} implements
 */
public class MultiThreadWordCount2 {
    static BlockingQueue<Map<String, Integer>> resultList = new LinkedBlockingQueue<>();
    static BlockingQueue<File> fileBlockingQueue = null;
    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        fileBlockingQueue = new LinkedBlockingQueue<>(files);

        IntStream.range(0, threadNum)
                .forEach(i -> new CountThread().start());

        lock.lock();
        try {
            while (resultList.size() < files.size()) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return MultiThreadWordCount1.flat(resultList);
    }

    static class CountThread extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                while (!fileBlockingQueue.isEmpty()) {
                    File file = fileBlockingQueue.poll();
                    if (file == null) {
                        break;
                    }
                    resultList.add(MultiThreadWordCount1.countOneFile(file));
                }
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}
