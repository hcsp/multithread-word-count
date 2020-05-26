package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SuyuZhuang
 * @date 2020/5/26 8:57 下午
 */
public class Utils {
    /**
     * 将currMap中词频信息合并到finalResult
     *
     * @param dest   合并后的结果
     * @param source 待合并的map
     */
    public static void mergeSourceMapToDest(Map<String, Integer> dest, Map<String, Integer> source) {
        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            Integer frequency = dest.getOrDefault(entry.getKey(), 0) + entry.getValue();
            dest.put(entry.getKey(), frequency);
        }
    }

    /**
     * 将多个map合并为一个map
     * @param mapList 待合并的map列表
     * @return Map<String, Integer> 合并后的map结果
     */
    public static Map<String, Integer> mergeMapList(List<Map<String, Integer>> mapList) {
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> oneMap : mapList) {
            for (Map.Entry<String, Integer> entry : oneMap.entrySet()) {
                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return result;
    }

    /**
     * 从单个文件file中按行读取，并计算词频
     *
     * @param file 单个文件
     * @return Map<String, Integer> 单词->词频
     */
    public static Map<String, Integer> countOneFile(File file) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Integer> wordToCountMap = new HashMap<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                Integer frequency = wordToCountMap.getOrDefault(word, 0) + 1;
                wordToCountMap.put(word, frequency);
            }
        }
        return wordToCountMap;
    }
}
