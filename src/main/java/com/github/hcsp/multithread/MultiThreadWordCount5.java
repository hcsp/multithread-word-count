package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount5 {
    // 使用 threadNum 个线程，并发统计文件中各单词的数量
    // 使用 ForkJoinPool 实现
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ForkJoinTask<Map<String, Integer>> task = new CountTask(
                files, 0, files.size(), Math.max(files.size() / threadNum, 1));
        return ForkJoinPool.commonPool().invoke(task);
    }

    private static class CountTask extends RecursiveTask<Map<String, Integer>> {
        List<File> files;
        int start;
        int end;
        final int THRESHOLD;

        private CountTask(List<File> files, int start, int end, int THRESHOLD) {
            this.files = files;
            this.start = start;
            this.end = end;
            this.THRESHOLD = THRESHOLD;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (end - start <= THRESHOLD) {
                // 如果任务足够小，就直接计算
                Map<String, Integer> CountResult = new HashMap<>();
                for (int i = start; i < end; i++) {
                    CountUtil.mergeSingleCountResultToFinalCountResult(
                            CountUtil.getCountResultFromSingleFile(files.get(i)), CountResult);
                }
                return CountResult;
            }
            int middle = (start + end) / 2;
            CountTask subtask1 = new CountTask(files, start, middle, THRESHOLD);
            CountTask subtask2 = new CountTask(files, middle, end, THRESHOLD);
            invokeAll(subtask1, subtask2);
            Map<String, Integer> subresult1 = subtask1.join();
            Map<String, Integer> subresult2 = subtask2.join();
            subresult1.forEach((key, value) -> subresult2.merge(key, value, Integer::sum));
            return subresult2;
        }
    }
}
