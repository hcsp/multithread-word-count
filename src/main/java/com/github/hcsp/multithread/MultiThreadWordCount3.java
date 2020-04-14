package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount3 {
    static ReentrantLock lock = new ReentrantLock();
    static Condition NotAllWorkersFinished = lock.newCondition();

     //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(files.size());
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<WorkerJob> allWorkerJobs = new ArrayList<>();
        for (File file : files) {
            WorkerJob workerJob = new WorkerJob(file, counter);
            executorService.submit(workerJob);
            allWorkerJobs.add(workerJob);
        }

        lock.lock();
        try {
            //如果有线程没工作完成，就等待
            while (counter.get() > 0) {
                NotAllWorkersFinished.await();
            }
            //如果都工作完了就开始合并线程的结果
            for (WorkerJob workerJob : allWorkerJobs) {
                CountFileUtils.mergeWorkerJobToFinalResult(finalResult, workerJob.getWordMapOfWorker());
            }


        } finally {
            lock.unlock();
        }

        return finalResult;

    }

    private static class WorkerJob implements Runnable {
        File file;
        AtomicInteger counter;
        HashMap<String, Integer> wordMapOfWorker;

        WorkerJob(File file, AtomicInteger counter) {
            this.file = file;
            this.counter = counter;
        }

        public HashMap<String, Integer> getWordMapOfWorker() {
            return wordMapOfWorker;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                wordMapOfWorker = CountFileUtils.countWord(file);
                //所有线程都处理完成了任务
                if (counter.decrementAndGet() == 0) {
                    NotAllWorkersFinished.signal();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

//    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
//        List<File> files = new ArrayList<>();
//        for (int i = 1; i < 3; i++) {
//            File file = new File("C:\\Users\\Dandan\\IdeaProjects\\multithread-word-count\\" + i + ".txt");
//            files.add(file);
//        }
//
//        Map<String, Integer> results = MultiThreadWordCount3.ultiThreadWordCount1.count(2, files);
//        System.out.println(results);
//
//    }

}
