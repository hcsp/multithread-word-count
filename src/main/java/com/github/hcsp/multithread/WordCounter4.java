package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

class WordCounter4 extends Thread {
    private final Result result;
    private final List<File> files;
    private final CyclicBarrier barrier;

    WordCounter4(Result result, List<File> files, CyclicBarrier barrier) {
        this.result = result;
        this.files = files;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        // 多个线程同时运行
        files.forEach(file -> ProcessFile.processFile(file).forEach((key, value) -> result.value.merge(key, value, Integer::sum)));
        try {
            barrier.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
