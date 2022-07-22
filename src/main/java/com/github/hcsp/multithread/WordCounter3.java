package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

class WordCounter3 extends Thread {
    final Result result;
    List<File> files;
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;

    WordCounter3(Result result, List<File> files, CountDownLatch startSignal, CountDownLatch doneSignal) {
        this.result = result;
        this.files = files;
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
    }

    @Override
    public void run() {
        // 多个线程同时运行
        try {
            startSignal.await();
            files.forEach(file -> ProcessFile.processFile(file).forEach((key, value) -> result.value.merge(key, value, Integer::sum)));
            doneSignal.countDown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
