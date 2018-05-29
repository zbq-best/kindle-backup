package com.ikyxxs.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Contents implements Serializable {
    private static final long serialVersionUID = -8246104929534874741L;

    /**
     * 异常信息
     */
    private String error;

    /**
     * 电子书信息
     */
    private List<Book> books;
}
