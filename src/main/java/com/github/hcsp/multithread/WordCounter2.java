package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class WordCounter2 extends Thread {
    final Result result;
    List<File> files;
    ReentrantLock lock;
    Condition completed;

    WordCounter2(Result result, List<File> files, ReentrantLock lock, Condition completed) {
        this.result = result;
        this.files = files;
        this.lock = lock;
        this.completed = completed;
    }

    @Override
    public void run() {
        // 多个线程同时运行
        lock.lock();
        files.forEach(file -> ProcessFile.processFile(file).forEach((key, value) -> result.value.merge(key, value, Integer::sum)));
        if (completed != null) {
            completed.signal();
        }
        lock.unlock();
    }
}
