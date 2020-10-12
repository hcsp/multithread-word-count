package com.github.hcsp.multithread;

import sun.plugin2.gluegen.runtime.CPU;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/****
 * 一个一个文件处理，
 * 每个线程执行相应行的操作
 * 使用Runnable无返回值形式
 * 共享集合 添加时用lock锁
 *
 * 主线程用CountDownLatch等待
 **/

public class MultiThreadWordCount1 {
    static final Map<String, Integer> reduce = new ConcurrentHashMap<>(16);

    static ThreadPoolExecutor executorService;

    static CountDownLatch countDownLatch;

    static Lock lock = new ReentrantLock();


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        executorService = new ThreadPoolExecutor(
                threadNum,
                threadNum,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1024),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        // 把一个文件分成10份
        final int count = 10;
        List<List<String>> lines = files.stream().map((file) -> FileUtils.getFileLines(file, count))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream).collect(Collectors.toList());
        countDownLatch = new CountDownLatch(lines.size());
        lines.forEach(fileLines -> executorService.execute(new Executor(fileLines)));
        try {
            countDownLatch.await();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return reduce;
    }


    public static class Executor implements Runnable {
        final List<String> lines;

        public Executor(List<String> lines) {
            this.lines = lines;
        }

        @Override
        public void run() {
            for (String line : this.lines) {
                analysis(line);
            }
            countDownLatch.countDown();
        }

        private void analysis(String line) {
            List<String> words = FileUtils.splitLineToWords(line);
            lock.lock();
            try {
                for (String word : words) {
                    if (reduce.containsKey(word)) {
                        reduce.put(word, reduce.get(word) + 1);
                    } else {
                        reduce.put(word, 1);
                    }
                }
            } finally {
                lock.unlock();
            }

        }


    }

}
