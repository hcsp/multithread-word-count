package com.github.hcsp.multithread;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool threadPool = new ForkJoinPool(threadNum);
        return threadPool.invoke(new ForkTask(files));
    }
}

class FileTask extends RecursiveTask<Map<String, Integer>> {

    private File file;

    FileTask(File file) {
        this.file = file;
    }

    @Override
    protected Map<String, Integer> compute() {
        return new WordCountTask().task(file);
    }
}

class ForkTask extends RecursiveTask<Map<String, Integer>> {
    private List<File> files;

    ForkTask(List<File> files) {
        this.files = files;
    }

    @Override
    protected Map<String, Integer> compute() {
        List<ForkJoinTask<Map<String, Integer>>> tasks = new ArrayList<>();
        for (File file : files) {
            FileTask fileTask = new FileTask(file);
            tasks.add(fileTask.fork());
        }
        Map<String, Integer> finalResult = new ConcurrentHashMap<>();
        for (ForkJoinTask<Map<String, Integer>> task : tasks) {
            new MergeWorker().mergeWorkerResultIntoFinalResult(task.join(), finalResult);
        }
        return finalResult;
    }
}
