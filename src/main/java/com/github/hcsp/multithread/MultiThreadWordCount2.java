package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

/**
 * CountDownLatch
 */
public class MultiThreadWordCount2 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        int corePoolSize = threadNum;
        int maximumPoolSize = threadNum;
        long keepAliveTime = 10;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(files.size());

        ThreadPoolExecutor pool = new ThreadPoolExecutor( corePoolSize,
                                                            maximumPoolSize,
                                                            keepAliveTime,
                                                            unit,
                                                            workQueue);

        CountDownLatch countDownLatch = new CountDownLatch(files.size());

        Map<String, Integer> result = new ConcurrentHashMap<>();

        files.forEach(file -> pool.submit(()->{
            Map<String, Long> wordMap = wordCount(file);
            synchronized(result){
                Set<String> set = wordMap.keySet();
                for (String key: set) {
                    result.put(key , result.getOrDefault(key,0) + wordMap.getOrDefault(key, 0L).intValue());
                }
                countDownLatch.countDown();
            }
        }));

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdown();
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
        System.out.println(count(1,Arrays.asList(
                new File("1.txt"),
                new File("2.txt"),
                new File("3.txt"),
                new File("4.txt")
        )));
    }

}
