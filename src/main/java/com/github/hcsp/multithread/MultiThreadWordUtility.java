package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sun peng
 *
 */
public class MultiThreadWordUtility {

    public static List<List<File>> fileSplit(int threadNum, List<File> files) {
        List<List<File>> fileChucks = new ArrayList<>();
        if (threadNum >= files.size()) {
            for (int i = 0; i < files.size(); i++) {
                fileChucks.add(files.subList(i, i + 1));
            }
            return fileChucks;
        } else {
            for (int j = 0; j < threadNum; j++) {
                fileChucks.add(new ArrayList<File>());
            }
            for (int i = 0; i < files.size(); i++) {
                fileChucks.get(i % threadNum).add(files.get(i));
            }
            return fileChucks;
        }
    }

    public static Map<String, Integer> fileChuckCount(List<File> files) throws IOException {
        Map<String, Integer> middleResult = new HashMap<>();
        for (File file : files) {
            BufferedReader bReader = Files.newBufferedReader(file.toPath());
            String line;
            while (null != (line = bReader.readLine())) {
                String[] strings = line.split(" ");
                for (String word : strings) {
                    middleResult.put(word, middleResult.getOrDefault(word, 0) + 1);
                }
            }
        }
        return middleResult;
    }
}
