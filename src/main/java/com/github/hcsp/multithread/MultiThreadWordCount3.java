package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        ForkJoinPool pool = ForkJoinPool.commonPool();
        FilesProcessor processor = new FilesProcessor(files, 0, files.size() - 1);
        pool.execute(processor);
        return processor.join();
    }

    private static class FilesProcessor extends RecursiveTask<Map<String, Integer>> {
        List<File> files;
        int start;
        int end;

        FilesProcessor(List<File> files, int start, int end) {
            this.files = files;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (start == end) {
                try {
                    return WordCounts.countSingleFile(files.get(start));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } else {
                int middle = start + (end - start) / 2 + 1;
                FilesProcessor left = new FilesProcessor(files, start, middle - 1);
                FilesProcessor right = new FilesProcessor(files, middle, end);
                left.fork();
                right.fork();
                Map<String, Integer> leftResult = left.join();
                Map<String, Integer> rightResult = right.join();
                WordCounts.mergeSubResult2Result(leftResult, rightResult);
                return rightResult;
            }
        }
    }
}
