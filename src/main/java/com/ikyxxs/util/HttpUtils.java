package com.ikyxxs.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class HttpUtils {

    private static OkHttpClient client = new OkHttpClient();

    /**
     * 通过HTTP GET方式获取网页内容
     *
     * @param url 链接
     * @return 网页内容
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return null != response.body() ? response.body().string() : StringUtils.EMPTY;
    }

    /**
     * 下载文件
     *
     * @param fileUrl  文件地址
     * @param fileName 文件名称
     */
    public static void downloadFile(String fileUrl, String fileName) {
        URL url;
        DataInputStream dataInputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            url = new URL(fileUrl);
            dataInputStream = new DataInputStream(url.openStream());

            fileOutputStream = new FileOutputStream(new File(fileName));

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }

        } catch (IOException e) {
            log.error("下载文件异常, fileUrl={}", fileUrl, e);
        } finally {
            try {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
            } catch (IOException e) {
                log.error("关闭文件输入流异常", e);
            }
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                log.error("关闭文件输出流异常", e);
            }
        }
    }
}
