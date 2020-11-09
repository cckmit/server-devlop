package com.glodon.pcop.spacialimportsvc.util;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class ZipFileUtil {
    private static Logger log = LoggerFactory.getLogger(ZipFileUtil.class);

    public static void unzip(String zipFilePath, String targetPath) {
        String pass_word = "";
        log.info("default pass_word empty string");
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipFile.setFileNameCharset("GBK");
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(pass_word);
            }
            zipFile.extractAll(targetPath);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // String input = "C:\\Users\\yuanjk\\Downloads\\全国省界、市界的行政边界数据_WGS1984.zip";
        String input = "G:\\Venice_no_overlap_bak\\road.zip";
        String output = "D:\\work\\temp\\road_unzip_v2";
        unzip(input, output);

        System.out.println(Charset.defaultCharset());
        System.out.println(System.getProperty("file.encoding"));
    }

}
