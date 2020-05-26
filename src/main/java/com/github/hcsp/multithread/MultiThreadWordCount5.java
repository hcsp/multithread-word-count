package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * parallelStream()
 */
public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        List<Map<String, Integer>> mapList = new ArrayList<>();
        files.parallelStream().forEach((file) -> {
            Map<String, Integer> oneFileMap = Utils.countOneFile(file);
            mapList.add(oneFileMap);
        });
        Map<String, Integer> result = Utils.mergeMapList(mapList);
        return result;
    }
}
