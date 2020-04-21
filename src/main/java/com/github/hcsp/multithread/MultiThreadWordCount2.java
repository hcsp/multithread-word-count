package com.github.hcsp.multithread;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static com.github.hcsp.multithread.MultiThreadWordCount5.count;
import static com.github.hcsp.multithread.MultiThreadWordCount5.merge;

/**
 * 使用CountDownLatch完成Word Count
 */
public class MultiThreadWordCount2 {
    public static void main(String[] args) throws Exception {
        List<File> files = Arrays.asList(
                new File("src/main/java/com/github/hcsp/multithread/1.txt"),
                new File("src/main/java/com/github/hcsp/multithread/2.txt"),
                new File("src/main/java/com/github/hcsp/multithread/3.txt")
        );
        Map<String, Integer> resultMap = new HashMap<>();
        List<Map<String, Integer>> resultList = new ArrayList<>();
        resultList = Collections.synchronizedList(resultList);
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        for (File file : files) {
            List<Map<String, Integer>> finalResultList = resultList;
            new Thread(() -> {
                Map<String, Integer> map = count(file);
                finalResultList.add(map);
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        for (Map<String, Integer> map : resultList) {
            resultMap = merge(resultMap, map);
        }
        System.out.println(resultMap);

    }
}
