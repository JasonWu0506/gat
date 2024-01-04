package com.pingcap.gat.util;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ExtractDirectoryStructure {

    public static void main(String[] args) {
        // 替换为你的文件全路径列表
        List<String> filePaths = List.of(
                "path/to/your/directory/file1.txt",
                "path/to/your/directory/subdirectory/file2.txt",
                "path/to/your/directory/file3.txt",
                "test"
        );

        // 抽取目录结构
        List<String> directoryStructure = extractDirectoryStructure(filePaths);

        // 打印目录结构
        directoryStructure.forEach(System.out::println);
    }

    public static List<String> extractDirectoryStructure(List<String> filePaths) {
        return filePaths.stream()
                .map(Path::of)
                .map(Path::getParent)
                .map(Path::toString)
                .distinct()
                .collect(Collectors.toList());
    }
}
