package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用ForkJoinPool()

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool pool = new ForkJoinPool(threadNum);
        List<Map<String, Integer>> mapList = new CopyOnWriteArrayList<>();
        for (File file : files) {
            Map<String, Integer> map = pool.invoke(new MyTask(file));
            mapList.add(map);
        }
        return CountUtil.mergeFileResult(mapList);


    }

    static class MyTask extends RecursiveTask<Map<String, Integer>> {
        File file;
        MyTask(File file) {
            this.file = file;
        }
        @Override
        protected Map<String, Integer> compute() {
            return CountUtil.countOneFile(file);
        }
    }
}
