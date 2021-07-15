package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;

public class GetWillProcessFileList {
    public static List<File> getWillProcessFileList(int threadNum, List<File> files, int i) {
        int fileLength = files.size();
        int threadExecuteFileNum = (int) Math.floor((double) fileLength / threadNum);
        int startIndex = i * threadExecuteFileNum;
        int endIndex = i == threadNum - 1 ? fileLength : (i + 1) * threadExecuteFileNum;
        return files.subList(startIndex, endIndex);
    }
}
