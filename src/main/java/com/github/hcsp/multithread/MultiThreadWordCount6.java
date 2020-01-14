package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

// parallelStream
public class MultiThreadWordCount6 {
    //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        return files.parallelStream()
                .map(file -> {
                    try {
                        return Common.countOneFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).reduce(new HashMap<>(), Common::mergeMaps);
    }
}
