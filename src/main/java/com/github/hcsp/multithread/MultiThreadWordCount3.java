package com.github.hcsp.multithread;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class MultiThreadWordCount3 {
    private static final LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(threadNum);

        List<List<File>> threadFile;
        List<Map<String, Integer>> resultList = new ArrayList<>(files.size());
        if (files.size() >= threadNum) {
            threadFile = Lists.partition(files, files.size() / threadNum);
        } else {
            threadFile = Lists.partition(files, 1);
        }

        for (List<File> fileList : threadFile) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (File file : fileList) {
                        resultList.add(FileUtil.count(file));
                    }
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        for (Map<String, Integer> map : resultList) {
            finalResult = FileUtil.merge(finalResult, map);
        }
        return finalResult;
    }
}
