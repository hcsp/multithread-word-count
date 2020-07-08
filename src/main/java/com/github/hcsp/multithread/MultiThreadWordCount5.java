package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

/**
 * ForkJoin
 */
public class MultiThreadWordCount5 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        //创建ForkJoin
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        //创建返回结果
        Map<String, Integer> result = new ConcurrentHashMap<>();

        List<ForkJoinTask> forkJoinTaskList = new ArrayList<>();

        files.forEach(file -> forkJoinTaskList.add(forkJoinPool.submit(() -> wordCount(file))));

        for (ForkJoinTask task : forkJoinTaskList) {
            try {
                Map<String, Long> wordMap = (Map<String, Long>) task.get();
                Set<String> keys = wordMap.keySet();
                for (String key : keys) {
                    result.put(key, result.getOrDefault(key, 0) + wordMap.getOrDefault(key, 0L).intValue());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    static Map<String, Long> wordCount(File file) {

        try {
            List<String> list = Files.readAllLines(file.toPath());
            return list.parallelStream().flatMap(i -> Arrays.stream(i.split(" ")))
                    .collect(Collectors.groupingBy(key -> key, counting()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println(count(4, Arrays.asList(
                new File("1.txt"),
                new File("2.txt"),
                new File("3.txt"),
                new File("4.txt")
        )));
    }

}
