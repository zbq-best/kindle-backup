package com.ikyxxs.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtils {

    /**
     * 根据链接获取文件名
     *
     * @param url 链接
     * @return 文件名
     */
    public static String getFileNameFromUrl(String url) {
        Pattern pattern = Pattern.compile("[^/\\\\\\\\]+$");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        }

        return StringUtils.EMPTY;
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    public static void deleteFile(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return;
        }

        File file = new File(fileName);
        if(file.exists()){
            file.deleteOnExit();
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     */
    public static void deleteFile(File file) {
        if (null == file) {
            return;
        }

        if (file.exists()) {
            file.deleteOnExit();
        }
    }

    /**
     * 创建文本文件并写入
     *
     * @param fileName 文件名
     * @param text 文本内容
     */
    public static File createText(String fileName, String text){
        FileWriter fw = null;
        BufferedWriter bw = null;
        File file = null;
        try{
            file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(text);
            bw.flush();
        } catch(Exception e) {
            log.error("创建本文异常", e);
        } finally {
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                log.error("关闭资源异常", e);
            }
            try {
                if (null != fw) {
                    fw.close();
                }
            } catch (IOException e) {
                log.error("关闭资源异常", e);
            }

        }
        return file;
    }

}
