package com.github.hcsp.multithread;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 并发流实现WordCount
 */
public class MultiThreadWordCount5 {
    //     使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        /*
       // 为什么这种写法可以通过测试，而且预期结果看起来是正确的。但看起来并不是线程安全的，我也没有进行同步操作
        Map<String, Integer> finalResult = new HashMap<>();
        files.parallelStream()
            .forEach(file -> {
                Map<String, Integer> wordCount = countSingleFile(file);
                merge(finalResult, wordCount);
            });
         return finalResult;
         */


        return files.parallelStream()
                .map(MultiThreadWordCount1::countSingleFile)
                .reduce(MultiThreadWordCount5::merge)
                .orElse(Collections.emptyMap());
    }

    private static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        MultiThreadWordCount1.merge(map1, map2);
        return map1;
    }
}
