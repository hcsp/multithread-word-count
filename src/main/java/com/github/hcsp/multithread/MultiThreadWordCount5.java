package com.github.hcsp.multithread;


import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/****
 * CompletableFuture 进行实现
 **/

public class MultiThreadWordCount5 {


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException,
            InterruptedException, BrokenBarrierException {

        Map<String, Integer> result = new HashMap<>();

        List<CompletableFuture<Map<String, Integer>>> completableFutureList = new ArrayList<>();
        List<List<File>> partition = Lists.partition(files, 1);
        for (List<File> fileList : partition) {
            completableFutureList.add(CompletableFuture.supplyAsync(() -> completableCount(fileList)));
        }

        CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[]{})).join();
        for (CompletableFuture<Map<String, Integer>> mapCompletableFuture : completableFutureList) {
            Map<String, Integer> stringIntegerMap = mapCompletableFuture.get();
            stringIntegerMap.forEach((word, count) -> result.merge(word, count, (a, b) -> a + b));
        }
        return result;
    }

    private static Map<String, Integer> completableCount(List<File> files) {
        return files.stream().map(FileUtils::readLines)
                .flatMap(Collection::stream)
                .map(FileUtils::splitLineToWords)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(x -> x, Collectors.summingInt(x -> 1)));
    }


}

