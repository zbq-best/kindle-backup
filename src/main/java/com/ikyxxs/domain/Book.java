package com.ikyxxs.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Book implements Serializable {
    private static final long serialVersionUID = 5284949441629622271L;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 修改时间
     */
    private Long modifiedTime;

    /**
     * 阅读记录修改时间
     */
    private LocalDateTime sdrModifiedTime;

    public Book(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Book book = (Book) o;
        return Objects.equals(fileName, book.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileName);
    }
}
