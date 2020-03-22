package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.github.hcsp.multithread.MultiThreadWordCount2.count2;
import static com.github.hcsp.multithread.MultiThreadWordCount2.merge2;

/**
 * 使用使用await,countDown方法代替wait,notify
 * 本质上都是一样的,目的是让所有线程操作完成后，才能开始合并，也就是达到阻塞的效果
 * 让CountDownLatch初值为文件总数，每分配一个线程CountDownLatch减一，等全部文件分配并执行完后，
 * await处等待的线程被唤醒，开始合并操作
 * 与此类似的还有condition,signal
 * await()方法会使线程等待，同时释放当前锁，当前线程加入Condition对象维护的等待队列中，
 * 当其他线程中使用signal()或signalAll()方法时，线程会重新获得锁继续执行
 * 这些本质上都是在进行PV操作，利用PV操作来实现操作原子性以及按照给定顺序执行
 */
public class MultiThreadWordCount4 {
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
        Map<String, Integer> result = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(files.size());
        List<Map<String, Integer>> workList = new ArrayList<>();
        long start = System.currentTimeMillis();
        ExecutorService service = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            //将每个文件包装成一个任务，任务的call方法写着对该文件的操作
            Work work = new Work(latch, file);
            //将任务提交给线程,并将结果记录
            workList.add(service.submit(work).get());
        }

        latch.await();
        for (Map<String, Integer> work : workList) {
            merge2(result, work);
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        return result;
    }

    private static class Work implements Callable<Map<String, Integer>> {
        CountDownLatch latch;
        File file;

        Work(CountDownLatch latch, File file) {
            this.latch = latch;
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> r = count2(file);
            latch.countDown();
            return r;

        }
    }
}
