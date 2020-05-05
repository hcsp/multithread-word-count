package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> finalResult = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(threadNum);
        List<Map<String, Integer>> resultList = new ArrayList<>(files.size());
        List<List<File>> threadFiles = Lists.partition(files, files.size() / threadNum);
        for (List<File> fileList : threadFiles) {
            new Thread(() -> {
                for (File file : fileList) {
                    System.out.println(Thread.currentThread().getName() + " 执行文件解析……");
                    resultList.add(WordCountUtil.count(file));
                }
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Map<String, Integer> map : resultList) {
            finalResult = WordCountUtil.merge(finalResult, map);

        }
        return finalResult;
    }
}
