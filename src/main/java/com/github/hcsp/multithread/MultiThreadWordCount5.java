package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinPool pool = new ForkJoinPool(threadNum);
        ForkJoinTask<Map<String, Integer>> sum = pool.submit(new SumTask(files));
        Map<String, Integer> map = new HashMap<>();
        try {
            map = sum.get();
            pool.shutdown();
            pool.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static class SumTask extends RecursiveTask<Map<String, Integer>> {
        private final List<File> files;

        SumTask(List<File> files) {
            this.files = files;
        }


        @Override
        protected Map<String, Integer> compute() {
            if (files.size() <= 1) {
                return doCount(files.get(0));
            } else {
                int middle = files.size() / 2;
                List<File> list1 = files.subList(0, middle);
                List<File> list2 = files.subList(middle, files.size());
                SumTask task1 = new SumTask(list1);
                SumTask task2 = new SumTask(list2);

                task1.fork();
                task2.fork();
                return mergeMap(task1.join(), task2.join());
            }
        }
    }

    public static Map<String, Integer> doCount(File file) {
        return FileUtil.count(file);
    }

    public static Map<String, Integer> mergeMap(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> map3 = new HashMap<>();
        map1.forEach((s, integer) -> {
            map3.put(s, integer + map2.getOrDefault(s, 0));
        });
        return map3;
    }
}
