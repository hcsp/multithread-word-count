package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

class WordCounts {

    private static final Object LOCK = new Object();

    /**
     * 将subResult合并到result中
     *
     * @param subResult 子集
     * @param result    合集
     */

    public static void mergeSubResult2Result(Map<String, Integer> subResult, Map<String, Integer> result) {
        for (Map.Entry<String, Integer> entry : subResult.entrySet()) {
            synchronized (LOCK) {
                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
    }

    /**
     * 统计单个文件词的频率
     *
     * @param file 单个文件路径
     * @return 返回一个词频率的Map
     */
    public static Map<String, Integer> countSingleFile(File file) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
        }
        return result;
    }
}
