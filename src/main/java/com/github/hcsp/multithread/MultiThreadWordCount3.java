package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> result = new HashMap<>();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        List<WorkJob> workList = new ArrayList<>();
        for (File file : files) {
            WorkJob work = new WorkJob(file);
            forkJoinPool.submit(work);
            workList.add(work);

        }
        for (WorkJob work : workList) {
            ReadFileUtils.mergeMap(result, work.join());
        }
        return result;
    }

    static class WorkJob extends RecursiveTask<Map<String, Integer>> {
        private Map<String, Integer> map;
        private File file;

        WorkJob(File file) {
            map = new HashMap<>();
            this.file = file;
        }

        public Map<String, Integer> getMap() {
            return map;
        }

        @Override
        public Map<String, Integer> compute() {
            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                ReadFileUtils.readWordsToMap(map, reader);
                return map;
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

}
