package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

/**
 * lock和condition
 */
public class MultiThreadWordCount4 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        //定义线程池
        int corePoolSize = threadNum;
        int maximumPoolSize = threadNum;
        long keepAliveTime = 10;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(files.size());

        //创建锁
        Lock lock = new ReentrantLock();
        //创建条件
        Condition condition = lock.newCondition();
        //总数
        AtomicInteger count = new AtomicInteger(files.size());

        //创建线程池
        ThreadPoolExecutor pool = new ThreadPoolExecutor( corePoolSize,
                                        maximumPoolSize,
                                        keepAliveTime,
                                        unit,
                                        workQueue);

        //创建返回结果
        Map<String ,Integer> result = new ConcurrentHashMap<>();

        files.forEach(file -> pool.submit(()->{

            //锁住
            lock.lock();
            try {
                //得到文件结果
                Map<String , Long> wordMap = wordCount(file);
                //合并
                Set<String> keys = wordMap.keySet();
                for (String key : keys) {
                    result.put(key,result.getOrDefault(key , 0) + wordMap.getOrDefault(key,0L).intValue());
                }
                //-1
                count.decrementAndGet();

                //唤醒
                condition.signal();
            }finally {
                lock.unlock();
            }
        }));


        //锁住
        lock.lock();
        try {
            while(count.get()>0){
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        //关闭线程池
        pool.shutdown();
        //返回结果
        return result;
    }


    static Map<String, Long> wordCount(File file){

        try {
            List<String> list = Files.readAllLines(file.toPath());
            return list.parallelStream().flatMap(i-> Arrays.stream(i.split(" ")))
                    .collect(Collectors.groupingBy(key -> key, counting()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(count(4,Arrays.asList(
                new File("1.txt"),
                new File("2.txt"),
                new File("3.txt"),
                new File("4.txt")
        )));
    }


}
