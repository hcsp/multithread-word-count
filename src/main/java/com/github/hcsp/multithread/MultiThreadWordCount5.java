package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        List<Map<String, Integer>> mapList = new ArrayList<>();
        files.parallelStream().forEach(file -> {
            try {
                mapList.add(MultiThreadWordCount1.countOneFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return MultiThreadWordCount1.merge(mapList);
    }
}
