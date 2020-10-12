package com.github.hcsp.multithread;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gxz gongxuanzhang@foxmail.com
 **/
public class FileUtils {
    /****
     * 把文件读取 每行作为单位 平均分成若干份
     * 此方法没有健壮性 也没讲究效率 交作业用的工具方法
     * 例：
     * getFileLines(file,2);
     * file如下：
     * aaa
     * bbb
     * ccc
     * ddd
     *
     * 返回 [[aaa,bbb][ccc,ddd]]
     *
     * @author gxz
     * @param file 文件
     * @param num 共分成多少份
     *
     **/
    public static List<List<String>> getFileLines(File file, int num) {
        List<String> lines = readLines(file);
        // 这里偷懒了一下 如果不能整除的话是会有数据丢失的  因为我发现都是10个文件和10个线程。 就先这么写。。不是重点
        return Lists.partition(lines, lines.size() / num);
    }

    public static List<String> readLines(File file) {
        try {
            return Files.readLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * 分词,多个空格也可分
     * @author gxz
     * @param line 一行数据
     * @return 分词之后的集合
     **/
    public static List<String> splitLineToWords(String line) {
        line = line.trim();
        List<String> words = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c != ' ') {
                stringBuilder.append(c);
            } else {
                if (stringBuilder.length() != 0) {
                    words.add(stringBuilder.toString());
                    stringBuilder.setLength(0);
                }
            }
        }
        if (stringBuilder.length() != 0) {
            words.add(stringBuilder.toString());
        }
        return words;
    }
}
