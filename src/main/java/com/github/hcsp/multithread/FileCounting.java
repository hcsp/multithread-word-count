package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FileCounting extends Thread {
    private ThreadLocal<Integer> processedFileIndex = new ThreadLocal<>();
    private BufferedReader reader;
    private List<File> files;
    private List<Map<String, Integer>> finalResult;
    private AtomicInteger completed;

    public FileCounting(BufferedReader reader, List<File> files, List<Map<String, Integer>> finalResult, AtomicInteger completed) {
        this.reader = reader;
        this.files = files;
        this.finalResult = finalResult;
        this.completed = completed;
    }

    @Override
    public void run() {
        processedFileIndex.set(0);
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) != null) {
                    finalResult.add(MultiThreadWordCount1.countSingleLine(line));
                } else if (MultiThreadWordCount1.hasFileToRead(files, processedFileIndex)) {
                    reloadFileReader();
                    continue;
                } else {
                    completed.addAndGet(1);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void reloadFileReader() {
        this.reader = MultiThreadWordCount1.getReader();
    }

    public Integer getProcessedFileIndex() {
        return processedFileIndex.get();
    }

    public void setProcessedFileIndex(int i) {
        this.processedFileIndex.set(i);
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Map<String, Integer>> getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(List<Map<String, Integer>> finalResult) {
        this.finalResult = finalResult;
    }
}
