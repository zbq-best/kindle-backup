package com.ikyxxs.util;

import com.alibaba.fastjson.JSON;
import com.ikyxxs.Application;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class QiniuUtils {

    //密钥配置
    private static Auth auth = Auth.create(Application.accessKey, Application.secretKey);

    //自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
    private static Zone z = Zone.autoZone();
    private static Configuration c = new Configuration(z);

    //创建上传对象
    private static UploadManager uploadManager = new UploadManager(c);

    //简单上传的Token
    private static String getUpToken() {
        return auth.uploadToken(Application.bucketName);
    }

    //定义下载地址过期时间为10分钟
    private static final Integer DOWNLOAD_URL_EXPIRE_IN_SECONDS = 600;

    //TODO 加缓存
    //覆盖上传的Token，和文件名关联
    private static String getUpToken(String fileName) {
        return auth.uploadToken(Application.bucketName, fileName);
    }

    /**
     * 上传文件到七牛(若存在则覆盖)
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     */
    public static Boolean upload(String filePath, String fileName) {
        try {
            //调用put方法上传
            Response res = uploadManager.put(filePath, fileName, getUpToken(fileName));

            //打印返回的信息
//            System.out.println(res.bodyString());
            return Boolean.TRUE;
        } catch (QiniuException e) {
            log.error("七牛云文件上传异常，fileName{}", fileName, e);
            return Boolean.FALSE;
        }
    }

    /**
     * 获取文件的下载链接
     *
     * @param fileName 文件名
     */
    public static String getDownloadUrl(String fileName) {
        String encodedFileName;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("获取七牛云文件下载链接异常，fileName{}", fileName, e);
            return StringUtils.EMPTY;
        }
        String publicUrl = String.format("%s/%s", Application.domainOfBucket, encodedFileName);

        return auth.privateDownloadUrl(publicUrl, DOWNLOAD_URL_EXPIRE_IN_SECONDS);
    }

    /**
     * 创建文本文件，然后上传七牛，删除本地临时文件
     *
     * @param fileName 文件名
     * @param content 内容
     */
    public static void createUploadAndDeleteText(String fileName, Object content) {
        //创建文本文件
        File file = FileUtils.createText(fileName, JSON.toJSONString(content));
        if (null != file) {
            //上传目录
            QiniuUtils.upload(file.getPath(), file.getName());
            //删除本地临时目录
            FileUtils.deleteFile(file);
        }
    }
}

