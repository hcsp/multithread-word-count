package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/****
 * 将所有文件分发给对应线程
 * 每个线程统计自己的数量 最后合并
 * 使用Callable
 * 主线程只需要get即可  get阻塞
 **/

public class MultiThreadWordCount2 {
    static final Map<String, Integer> reduce = new ConcurrentHashMap<>(16);

    static ThreadPoolExecutor executorService;


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException,
            InterruptedException {
        executorService = new ThreadPoolExecutor(
                threadNum,
                threadNum,
                10,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1024),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        // 这里参数写1 就是平均分成10份 其实是偷懒写法。。 应该用算法计算每个线程的工作数
        List<List<File>> fileBox = Lists.partition(files, 1);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (List<File> box : fileBox) {
            futures.add(executorService.submit(new Executor(box)));
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> countMap = future.get();
            countMap.forEach((word, count) -> reduce.merge(word, count, (a, b) -> a + b));
        }
        return reduce;
    }

    public static class Executor implements Callable<Map<String, Integer>> {
        final List<File> files;

        public Executor(List<File> files) {
            this.files = files;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            return files.stream()
                    .map(FileUtils::readLines)
                    .filter(Objects::nonNull) // 切割成多行
                    .flatMap(Collection::stream)   // 每行独立
                    .map(FileUtils::splitLineToWords)  //分词
                    .flatMap(Collection::stream)
                    .collect(Collectors.groupingBy(x -> x, Collectors.summingInt(x -> 1)));// 计算数量
        }


    }

}
