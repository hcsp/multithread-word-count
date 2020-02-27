package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * {@link java.util.concurrent.Future} / {@link java.util.concurrent.ExecutorService} implements
 */
public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(
                threadNum,
                threadNum,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(20),
                Thread::new,
                new ThreadPoolExecutor.DiscardPolicy()
        );

        List<Future<Map<String, Integer>>> futureList = new ArrayList<>();
        for (List<File> fileList : Lists.partition(files, threadNum)) {
            futureList.add(executorService.submit(new CountCallable(fileList)));
        }

        executorService.shutdown();

        List<Map<String, Integer>> mapList = new ArrayList<>();
        for (Future<Map<String, Integer>> f : futureList) {
            mapList.add(f.get());
        }

        return MultiThreadWordCount1.flat(mapList);
    }

    static class CountCallable implements Callable<Map<String, Integer>> {
        List<File> fileList;
        BlockingQueue<Map<String, Integer>> resultQueue;

        public CountCallable(List<File> fileQueue) {
            this.fileList = fileQueue;
            this.resultQueue = new LinkedBlockingQueue<>();
        }

        @Override
        public Map<String, Integer> call() {
            for (File file : fileList) {
                resultQueue.add(MultiThreadWordCount1.countOneFile(file));
            }
            return MultiThreadWordCount1.flat(resultQueue);
        }
    }
}

