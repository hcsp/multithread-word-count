package com.github.hcsp.multithread;


import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/****
 * 使用队列进行统计
 * 消费线程实现分词添加
 * 最终主线程统一count
 **/

public class MultiThreadWordCount4 {

    static List<String> list = new CopyOnWriteArrayList<>();

    static final Queue<String> queue = new ConcurrentLinkedQueue<>();

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
                .forEach(queue::offer);

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
                String word = queue.poll();
                if (word == null) {
                    countDownLatch.countDown();
                    break;
                }
                list.add(word);
            }
        }
    }
}

