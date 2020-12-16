package com.github.hcsp.multithread;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetWordInFile {
    static Map<String, Integer> getWordCountFormFile(File file) {
        List<String> strings;
        try {
            strings = Files.readAllLines(file.toPath());
            return getWordsCountInMultiLines(strings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    static Map<String, Integer> getWordsCountInMultiLines(List<String> lines) {
        Map<String, Integer> count = new HashMap<>();
        for (String line : lines) {
            Map<String, Integer> wordsCountInLine = getWordsCountInLine(line);
            mapAdd(count, wordsCountInLine);
        }
        return count;
    }

    private static Map<String, Integer> getWordsCountInLine(String line) {
        Map<String, Integer> count = new HashMap<>();
        String[] wordArray = line.split(" ");
        for (String word : wordArray) {
            count.put(word, count.getOrDefault(word, 0) + 1);
        }
        return count;
    }

    static void mapAdd(Map<String, Integer> map1, Map<String, Integer> map2) {
        map2.forEach((s, integer) -> map1.put(s, map1.getOrDefault(s, 0) + integer));
    }
}

