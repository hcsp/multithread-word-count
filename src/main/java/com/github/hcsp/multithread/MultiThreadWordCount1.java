package com.github.hcsp.multithread;


import com.google.common.collect.Lists;

import java.io.File;
import java.security.Policy;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/****
 * forkJoin实现
 **/

public class MultiThreadWordCount1 {

    static int threshold;

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException,
            InterruptedException, BrokenBarrierException {
        // 所有的行
        List<String> allLines = files.stream().map(FileUtils::readLines)
                .flatMap(Collection::stream)
                .map(FileUtils::splitLineToWords)
                .flatMap(Collection::stream).collect(Collectors.toList());
        // 阈值随便设置一个
        threshold = allLines.size() / threadNum;
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Map<String, Integer>> task = new ForkJoinExecutor(allLines);
        pool.invoke(task);
        return task.get();
    }


    public static class ForkJoinExecutor extends RecursiveTask<Map<String, Integer>> {
        List<String> lines;

        ForkJoinExecutor(List<String> lines) {
            this.lines = lines;
        }

        @Override
        protected Map<String, Integer> compute() {
            if (lines.size() < threshold) {
                return lines.stream().collect(Collectors.groupingBy(x -> x, Collectors.summingInt(x -> 1)));
            } else {
                List<List<String>> partition = Lists.partition(lines, lines.size() / 2 + 1);
                // 这里一定会切割出两个
                ForkJoinExecutor pre = new ForkJoinExecutor(partition.get(0));
                pre.fork();
                ForkJoinExecutor next = new ForkJoinExecutor(partition.get(1));
                next.fork();
                Map<String, Integer> preJoin = pre.join();
                Map<String, Integer> nextJoin = next.join();
                preJoin.forEach((word, count) -> nextJoin.merge(word, count, (a, b) -> a + b));
                return nextJoin;
            }
        }

    }
}



