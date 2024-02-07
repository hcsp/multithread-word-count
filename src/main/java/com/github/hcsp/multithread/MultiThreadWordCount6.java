package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount6 {
    //Collection.parallelStream()
    //按照线程数分配给各个任务,并行的执行
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        return files.parallelStream().map(MultiThreadWordCount6::readAndCount).reduce(new HashMap<>(), MultiThreadWordCount6::mergeMap);
    }

    private static Map<String, Integer> readAndCount(File file) {
        Map<String, Integer> result = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Arrays.stream(line.split(" ")).forEach(word -> result.merge(word, 1, Integer::sum));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static Map<String, Integer> mergeMap(Map<String, Integer> map1, Map<String, Integer> map2) {
        Map<String, Integer> mergeResult = new HashMap<>();
        map1.forEach((key, value) -> mergeResult.merge(key, value, Integer::sum));
        map2.forEach((key, value) -> mergeResult.merge(key, value, Integer::sum));
        return mergeResult;
    }


}
