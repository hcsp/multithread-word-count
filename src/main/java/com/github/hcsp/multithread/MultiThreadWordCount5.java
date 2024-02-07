package com.github.hcsp.multithread;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount5 {
    //ForkJoinPool, 在ForkJoinTask内部递归fork出新的任务,
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        int chunkNum = files.size() / threadNum; //分块运算
        //ForkJoinPool进行提交, 不指定最大线程数
        Future<Map<String, Integer>> future = new ForkJoinPool().submit(new ReadAndCount(files, chunkNum, 0, files.size()));
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ReadAndCount extends RecursiveTask<Map<String, Integer>> {
        private final List<File> files;
        private final int chunkNum;
        private final int start;
        private final int end;

        ReadAndCount(List<File> files, int chunkNum, int start, int end) {
            this.files = files;
            this.chunkNum = chunkNum;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (end - start == chunkNum) {
                return files.subList(start, end).stream().map(ReadAndCount::fileToMap).reduce(ReadAndCount::mergeMap).get();
            } else {
                int mid = start + ((end - start) >> 1); //位运算优先级低, 注意括号
                ForkJoinTask<Map<String, Integer>> leftFork = new ReadAndCount(files, chunkNum, start, mid).fork();
                ForkJoinTask<Map<String, Integer>> rightFork = new ReadAndCount(files, chunkNum, mid, end).fork();
                return mergeMap(leftFork.join(), rightFork.join());
            }
        }

        private static Map<String, Integer> mergeMap(Map<String, Integer> map1, Map<String, Integer> map2) {
            Map<String, Integer> mergeResult = new HashMap<>();
            map1.forEach((key, value) -> mergeResult.merge(key, value, Integer::sum));
            map2.forEach((key, value) -> mergeResult.merge(key, value, Integer::sum));
            return mergeResult;
        }

        private static Map<String, Integer> fileToMap(File file) {
            Map<String, Integer> result = new HashMap<>();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Arrays.stream(line.split(" ")).forEach(word -> result.merge(word, 1, Integer::sum));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return result;
        }
    }
}
