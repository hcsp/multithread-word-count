package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountUtil {
    //统计单个文件词频
    static Map<String, Integer> countOneFile(File file) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //合并不同文件统计结果

    static Map<String, Integer> mergeFileResult(List<Map<String, Integer>> mapList) {
        Map<String, Integer> finalResult = new HashMap<>();
        for (Map<String, Integer> map : mapList) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String word = entry.getKey();
                finalResult.put(word, finalResult.getOrDefault(word, 0) + entry.getValue());

            }

        }
        return finalResult;

    }
}
