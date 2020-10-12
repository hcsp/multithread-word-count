package com.github.hcsp.multithread;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import sun.plugin2.gluegen.runtime.CPU;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/****
 * 将所有文件分发给对应线程
 * 每个线程统计自己的数量 (在每个线程中合并  这次用读写锁操作)
 * 使用Runnable
 *
 * 使用 CyclicBarrier实现唤醒(没必要 但也能实现)
 **/

public class MultiThreadWordCount3 {
    static final Map<String, AtomicInteger> reduce = new ConcurrentHashMap<>(16);

    static ThreadPoolExecutor executorService;

    static  CyclicBarrier cyclicBarrier;

    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException,
            InterruptedException {
        executorService = new ThreadPoolExecutor(
                threadNum,
                threadNum,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1024),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        List<List<File>> fileBox = Lists.partition(files, threadNum);
        for (List<File> box : fileBox) {
            Future<Map<String, Long>> submit = executorService.submit(new Executor(box));
            Map<String, Long> countMap = submit.get();
            countMap.forEach((word,count)->{
                if(reduce.containsKey(word)){
                    reduce.put(word,(int)(reduce.get(word)+count));
                }else{
                    reduce.put(word,(int)(count+0)); // 这里为了强转  一定不会出现超过int的情况
                }
            });
        }

        return reduce;
    }


    public static class Executor implements Runnable {
        final List<File> files;

        public Executor(List<File> files) {
            this.files = files;
        }

        @Override
        public void run() {
            Map<String, Long> countMap = files.stream()
                    .map(FileUtils::readLines)
                    .filter(Objects::nonNull) // 切割成多行
                    .flatMap(Collection::stream)   // 每行独立
                    .map(FileUtils::splitLineToWords)  //分词
                    .flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(x -> x, Collectors.counting()));// 计算数量
            countMap.forEach((word,count)->{
                if(reduce.containsKey(word)){
                    reduce.get(word).addAndGet((int)(count+0));
                }else{
                    reduce.put(word,new AtomicInteger());
                }
            });
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

}
