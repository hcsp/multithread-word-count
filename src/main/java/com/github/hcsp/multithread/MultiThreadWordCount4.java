package com.github.hcsp.multithread;


import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/****
 * 使用队列进行统计
 * 消费线程实现分词添加
 * 最终主线程统一count
 **/

public class MultiThreadWordCount4 {

    static List<String> list = new CopyOnWriteArrayList<>();

    static final Queue<String> QUEUE = new ConcurrentLinkedQueue<>();

    static ThreadPoolExecutor executorService;

    static CountDownLatch countDownLatch;


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException,
            InterruptedException, BrokenBarrierException {
        executorService = new ThreadPoolExecutor(
                threadNum,
                threadNum,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1024),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        files.stream().map(FileUtils::readLines)
                .flatMap(Collection::stream)
                .map(FileUtils::splitLineToWords)
                .flatMap(Collection::stream)
                .forEach(QUEUE::offer);

        countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(new Consumer());
        }
        countDownLatch.await();
        return list.stream().collect(Collectors.groupingBy(x -> x, Collectors.summingInt(x -> 1)));
    }


    public static class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                String word = QUEUE.poll();
                if (word == null) {
                    countDownLatch.countDown();
                    break;
                }
                list.add(word);
            }
        }
    }
}

