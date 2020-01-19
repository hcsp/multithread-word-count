package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    //多线程线程操作files, 且文件内容也由多线程读取
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        //创建files 的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(files.size());

        List<Future<Map<String, Integer>>> fileFutures = new ArrayList<>();

        //为每个file线程分配任务
        for (File file : files) {
            fileFutures.add(threadPool.submit(new FileWorkerJob(file, threadNum)));
        }

        //合并每个file线程,返回最终结果
        return getFilesFinalResult(fileFutures);
    }

    public static Map<String, Integer> getFilesFinalResult(List<Future<Map<String, Integer>>> fileFutures) throws InterruptedException, ExecutionException {
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future :
                fileFutures) {
            Map<String, Integer> resultFromWorker = future.get();

            WordCount.mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        return finalResult;
    }

    //file 的工作说明书
    private static class FileWorkerJob implements Callable<Map<String, Integer>> {
        File file;
        int threadNum;

        FileWorkerJob(File file, int threadNum) {
            this.file = file;
            this.threadNum = threadNum;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            WordCount fileWordCount = new WordCount(threadNum);

            return fileWordCount.count(file);
        }
    }
}
