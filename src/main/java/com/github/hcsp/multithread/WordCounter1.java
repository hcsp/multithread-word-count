package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;

class WordCounter1 extends Thread {
    final Result result;
    List<File> files;
    boolean isLast;

    WordCounter1(Result result, List<File> files, boolean isLast) {
        this.result = result;
        this.files = files;
        this.isLast = isLast;
    }

    @Override
    public void run() {
        // 多个线程同时运行
        synchronized (result) {
            files.forEach(file -> ProcessFile.processFile(file).forEach((key, value) -> result.value.merge(key, value, Integer::sum)));
            if (isLast) {
                result.notify();
            }
        }
    }
}
