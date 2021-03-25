package com.github.hcsp.multithread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiThreadWordCount1 {


    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
        ExecutorService executorService = getThreadPool(threadNum);
        CountDownLatch latch = new CountDownLatch(files.size());
        ArrayList<Future<Map<String, Integer>>> futures = new ArrayList<>(files.size());
        for (File file : files) {
            Future<Map<String, Integer>> future = executorService.submit(() -> countOneFile(file, latch));
            futures.add(future);
        }
        latch.await();
        return mergeAllCountResult(futures);
    }

    private static Map<String, Integer> mergeAllCountResult(ArrayList<Future<Map<String, Integer>>> futures) throws ExecutionException, InterruptedException {
        Map<String, Integer> countResult = new HashMap<>();
        for (Future<Map<String, Integer>> f : futures) {
            Map<String, Integer> map = f.get();
            map.entrySet().forEach(s -> {
                String word = s.getKey();
                Integer thisCount = s.getValue();
                Integer currentCount = countResult.getOrDefault(word, 0);
                countResult.put(word, currentCount + thisCount);
            });
        }
        return countResult;
    }

    /**
     * 构造线程池
     *
     * @param threadNum 固定线程数
     * @return thread pool
     */
    public static ExecutorService getThreadPool(int threadNum) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("count-pool-%d").build();
        return new ThreadPoolExecutor(threadNum, threadNum,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 计算一个文件的单词个数
     *
     * @param file 文件
     * @return 计数Map
     * @throws IOException e
     */
    private static Map<String, Integer> countOneFile(File file, CountDownLatch latch) throws IOException {
        HashMap<String, Integer> countMap = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] s = line.split(" ");
            Arrays.stream(s).forEach(w -> {
                Integer count = countMap.getOrDefault(w, 0);
                countMap.put(w, ++count);
            });
        }
        latch.countDown();
        return countMap;
    }
}
