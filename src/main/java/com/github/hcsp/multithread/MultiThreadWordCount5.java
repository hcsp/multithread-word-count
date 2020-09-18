package com.github.hcsp.multithread;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

/**
 * parallelStream + 自定义线程池
 * 为什么不使用默认线程池，因为默认线程池是全局公用的，如果在其他地方有阻塞会导致该任务也阻塞
 */
public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
        public static Map<String, Integer> count(int threadNum, List<File> files) {
            Map<String, Integer> map = new HashMap<>();
            ForkJoinPool customThreadPool = new ForkJoinPool(threadNum);
            try {
                map = customThreadPool.submit(
                        () -> files.parallelStream().map(MultiThreadWordCount4::doCount).reduce(MultiThreadWordCount4::mergeMap)
                ).get().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
//            Optional<Map<String, Integer>> optional = files.parallelStream().map(MultiThreadWordCount4::doCount).reduce(MultiThreadWordCount4::mergeMap);
//            return optional.get();
            return map;
        }
}
