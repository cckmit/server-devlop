
package com.glodon.pcop.weasvc.util;

import java.io.File;


/**
 * Created by luorh on 2017/5/26.
 */
public class FileUtils {
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }


    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

}
