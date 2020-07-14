package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {


    public static Map<String, Integer> mergeMap(Map<String, Integer> targetMap, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : targetMap.entrySet()) {
            finalResult.put(entry.getKey(), finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
        return finalResult;
    }


    public static Map<String, Integer> mergeMapList(List<Map<String, Integer>> mapList) {
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> oneMap : mapList) {
            for (Map.Entry<String, Integer> entry : oneMap.entrySet()) {
                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return result;
    }



    public static Map<String, Integer> countFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Map<String, Integer> countResult = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] wordsArr = line.split(" ");
            for (String word : wordsArr) {
                countResult.put(word, countResult.getOrDefault(word, 0) + 1);
            }
        }
        return countResult;
    }

}

