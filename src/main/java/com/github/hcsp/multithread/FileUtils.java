package com.github.hcsp.multithread;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileUtils {
    public static List<String> readFileContent(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String strLine = null;
        List<String> content = new ArrayList<>();
        Map<String, Integer> countMap = new ConcurrentHashMap<>();
        while (null != (strLine = bufferedReader.readLine())) {
            content.add(strLine);
        }
        bufferedReader.close();
        return content;
    }
}
