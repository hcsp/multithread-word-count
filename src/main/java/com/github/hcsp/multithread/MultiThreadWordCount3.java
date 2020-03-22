package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.hcsp.multithread.MultiThreadWordCount2.count2;
import static com.github.hcsp.multithread.MultiThreadWordCount2.merge2;

/**
 * 使用Object.wait/notify
 * wait/notify在这里的作用在于阻塞等待所用线程的操作，等所有线程都计算完毕后，再将结果汇总
 * 在功能上类似于Future,而Future是将结果暂存，通过一个阻塞的方法get()拿到最终的结果
 * 统计单词的操作都是相同的，可以复用其他类里已经写好的方法，为每个文件分配一个线程，当文件全部统计完，才能开始合并
 */
public class MultiThreadWordCount3 {
    static final Object lock = new Object();

    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
        Map<String, Integer> result = new HashMap<>();
        AtomicInteger restFilesCount = new AtomicInteger(files.size());
        List<Map<String, Integer>> workList = new ArrayList<>();
        long start = System.currentTimeMillis();
        ExecutorService service = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            //将每个文件包装成一个任务，任务的call方法写着对该文件的操作
            Work work = new Work(restFilesCount, file);
            //将任务提交给线程,并将结果记录
            workList.add(service.submit(work).get());
        }
        synchronized (lock) {
            /**
             * 当有线程进入时若还有文件没有统计完，便释放锁阻塞等待，wait()方法会释放当前锁
             */
            if (restFilesCount.get() > 0) {
                lock.wait();
            }
            for (Map<String, Integer> work : workList) {
                merge2(result, work);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return result;
    }


    private static class Work implements Callable<Map<String, Integer>> {
        AtomicInteger restFilesCount;
        File file;
        Map<String, Integer> result = new HashMap<>();

        Work(AtomicInteger restFilesCount, File file) {
            this.restFilesCount = restFilesCount;
            this.file = file;
        }

        /**
         * notify会唤醒一个在当前锁上等待的线程，但notify不释放锁，只有等当前线程运行完后，锁才会释放
         */
        @Override
        public Map<String, Integer> call() throws Exception {
            synchronized (lock) {
                if (restFilesCount.decrementAndGet() == 0) {
                    lock.notify();
                }
                return count2(file);
            }
        }
    }
}
