package com.ikyxxs;

import com.alibaba.fastjson.JSON;
import com.ikyxxs.domain.Book;
import com.ikyxxs.domain.Contents;
import com.ikyxxs.util.FileUtils;
import com.ikyxxs.util.HttpUtils;
import com.ikyxxs.util.QiniuUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    //七牛账号的ACCESS_KEY
    public static String accessKey;

    //七牛账号的SECRET_KEY
    public static String secretKey;

    //七牛账号的存储空间
    public static String bucketName;

    //七牛账号的空间域名
    public static String domainOfBucket;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${qiniu.accessKey}")
    public void setAccessKey(String accessKey) {
        Application.accessKey = accessKey;
    }

    @Value("${qiniu.secretKey}")
    public void setSecretKey(String secretKey) {
        Application.secretKey = secretKey;
    }

    @Value("${qiniu.bucketName}")
    public void setBucketName(String bucketName) {
        Application.bucketName = bucketName;
    }

    @Value("${qiniu.domainOfBucket}")
    public void setDomainOfBucket(String domainOfBucket) {
        Application.domainOfBucket = domainOfBucket;
    }

    @Override
    public void run(String... args) {
        log.info("开始同步Kindle...");

        //获取云端的目录信息
        String contentsUrl = QiniuUtils.getDownloadUrl("Contents.txt");
        Contents contents;
        try {
            contents = JSON.parseObject(HttpUtils.get(contentsUrl), Contents.class);
            if (StringUtils.isNotBlank(contents.getError())) {
                log.error("获取云端目录出错：" + contents.getError());
            }
        } catch (Exception e) {
            log.error("获取云端目录异常", e);
            contents = new Contents();
        }

        //将目录存入缓存
        Map<String, Book> bookMap  = new HashedMap<>();
        Set<Book> bookSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(contents.getBooks())) {
            log.info("云端电子书数量：" + contents.getBooks().size());
            contents.getBooks().forEach(book -> bookMap.put(book.getFileName(), book));
        }

        //遍历本地documents目录并上传电子书
        File dir = new File("documents");
        if (!dir.exists() && !dir.isDirectory()) {
            log.error("获取documents目录异常");
            return;
        }
        File[] files = dir.listFiles();
        if (null != files && files.length > 0) {
            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }

                Book book;
                if (bookMap.containsKey(file.getName())) {
                    book = bookMap.get(file.getName());
                } else {
                    book = new Book(file.getName());
                }

                if (null == book.getModifiedTime() || file.lastModified() >  book.getModifiedTime()) {
                    log.info("正在上传：" + file.getName());
                    Boolean result = QiniuUtils.upload(file.getPath(), file.getName());
                    if (result) {
                        book.setModifiedTime(file.lastModified());
                        bookSet.add(book);
                    }
                }
            }
        }
        log.info("上传完成，共上传电子书数量：" + bookSet.size());

        Contents newContents = new Contents();
        bookSet.addAll(bookMap.values());
        newContents.setBooks(new LinkedList<>(bookSet));

        //创建本地临时目录
        File contentsFile = FileUtils.createText("Contents.txt", JSON.toJSONString(newContents));
        if (null != contentsFile) {
            //上传目录
            QiniuUtils.upload(contentsFile.getPath(), contentsFile.getName());
            //删除本地临时目录
            FileUtils.deleteFile(contentsFile);
        }
    }
}
