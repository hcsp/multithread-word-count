package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.concurrent.Semaphore;

public class WordCounter5 extends Thread {
    private final Result result;
    private final List<File> files;
    private final Semaphore emptySlot;
    private final Semaphore fullSlot;

    public WordCounter5(Result result, List<File> files, Semaphore emptySlot, Semaphore fullSlot) {
        this.result = result;
        this.files = files;
        this.emptySlot = emptySlot;
        this.fullSlot = fullSlot;
    }

    @Override
    public void run() {
        try {
            emptySlot.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        files.forEach(file -> ProcessFile.processFile(file).forEach((key, value) -> result.value.merge(key, value, Integer::sum)));
        fullSlot.release();
    }
}
