package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class WordCount {
    public WordCount(int threadNum) {
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws IOException {
        Map<String, Integer> numberOfWord = new HashMap<>();
        StringTokenizer stringTokenizer = dealWithStringFromFile(file);
        while (stringTokenizer.hasMoreTokens()) {
            String aWord = stringTokenizer.nextToken();
            if (numberOfWord.containsKey(aWord)) {
                int count = numberOfWord.get(aWord);
                numberOfWord.put(aWord, count + 1);
            } else {
                numberOfWord.put(aWord, 1);
            }
        }

        System.out.println(numberOfWord);
        return numberOfWord;
    }

    /**
     * Processing the content of the file into a string
     *
     * @param file
     * @return StringTokenizer
     * @throws IOException
     */

    public static StringTokenizer dealWithStringFromFile(File file) throws IOException {
        List<String> stringFromFile = Files.readAllLines(file.toPath());
        StringTokenizer string;
        string = new StringTokenizer(stringFromFile.toString()
                .replace("[", " ")
                .replace("]", " ")
                .replace(",", " "));
        return string;

    }
}

