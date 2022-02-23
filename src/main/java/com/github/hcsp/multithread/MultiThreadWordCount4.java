package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用parallelStream()

    public static Map<String, Integer> count(int threadNum, List<File> files) {
        String threadNumStr = String.valueOf(threadNum);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", threadNumStr);
        List<Map<String, Integer>> mapList = new CopyOnWriteArrayList<>();
        files.parallelStream().forEach(file -> {
            Map<String, Integer> oneMap = CountUtil.countOneFile(file);
            mapList.add(oneMap);
        });
        return CountUtil.mergeFileResult(mapList);
    }

}
