package com.github.hcsp.multithread;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 并发流 实现
 *
 * @author kwer
 * @date 2020/5/5 22:33
 */
public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Optional<Map<String, Integer>> optionalMap = files.parallelStream().map(WordCountUtil::count).reduce(WordCountUtil::merge);
        return optionalMap.get();
    }
}
