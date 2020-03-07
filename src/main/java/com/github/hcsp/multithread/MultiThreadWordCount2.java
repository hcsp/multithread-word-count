package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 线程池加callable
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService es = Executors.newFixedThreadPool(threadNum);
        List<Future<HashMap<String, Integer>>> futures = new ArrayList<>();

        files.forEach(file -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                for (int i = 0; i < threadNum; i++) {
                    futures.add(es.submit(new Worker(br)));
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        HashMap<String, Integer> finalMap = new HashMap<>();
        futures.forEach(future -> {
            try {
                ReaderUtils.mergeMapToMap(future.get(), finalMap);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        return finalMap;
    }

    static class Worker implements Callable<HashMap<String, Integer>> {
        private BufferedReader bf;

        Worker(BufferedReader bf) {
            this.bf = bf;
        }

        @Override
        public HashMap<String, Integer> call() {
            HashMap<String, Integer> map;
            try {
                map = ReaderUtils.readFileAsMap(bf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return map;
        }
    }
}
