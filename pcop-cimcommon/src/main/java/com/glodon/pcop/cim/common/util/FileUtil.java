package com.glodon.pcop.cim.common.util;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    /**
     * 删除文件
     *
     * @param path
     * @param isDeleteSubFiles 是否删除子文件
     * @return
     */
    public static boolean deleteFile(Path path, boolean isDeleteSubFiles) {
        try {
            if (!isDeleteSubFiles) {
                Files.deleteIfExists(path);
                return true;
            } else {
                if (Files.isDirectory(path)) {
                    try (DirectoryStream<Path> entires = Files.newDirectoryStream(path)) {
                        for (Path tmpPath : entires) {
                            deleteFile(tmpPath, true);
                        }
                    }
                }
                Files.deleteIfExists(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
