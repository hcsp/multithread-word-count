package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ReadFileWithThread extends Thread {
    //    private List<File> files;
    private File file;
    private Map<String, Integer> map;

    public ReadFileWithThread(File file) {
        map = new HashMap<>();
        this.file = file;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    @Override
    public void run() {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            ReadFileUtils.readWordsToMap(map, reader);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("file not found" + file);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
