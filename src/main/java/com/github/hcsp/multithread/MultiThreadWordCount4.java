package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        MultiThreadWordCount2.result.clear();
        files.parallelStream().forEach(MultiThreadWordCount2::countOneFile);
        return MultiThreadWordCount2.result;
    }
}
