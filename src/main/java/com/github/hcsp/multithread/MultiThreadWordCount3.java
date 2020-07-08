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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

/**
 * notify和wait
 */
public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        //定义线程池
        int corePoolSize = threadNum;
        int maximumPoolSize = threadNum;
        long keepAliveTime = 10;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(files.size());
        //创建线程池
        ThreadPoolExecutor pool = new ThreadPoolExecutor( corePoolSize,
                                                            maximumPoolSize,
                                                            keepAliveTime,
                                                            unit,
                                                            workQueue);
        //
        AtomicInteger count = new AtomicInteger(files.size());

        //创建返回结果
        Map<String, Integer> result = new ConcurrentHashMap<>();

        files.forEach(file -> pool.submit(()->{
            synchronized(result){
                //得到文件结果
                Map<String , Long> wordMap = wordCount(file);
                //合并
                Set<String> keys = wordMap.keySet();
                for (String key : keys) {
                    result.put(key,result.getOrDefault(key , 0) + wordMap.getOrDefault(key,0L).intValue());
                }
                //总数-1
                count.decrementAndGet();
                //唤醒
                result.notify();
            }
        }));

        synchronized (result){
            //等待所有任务结束
            while (count.get()>0){
                //任务没结束就继续等待
                try {
                    result.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //返回
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
