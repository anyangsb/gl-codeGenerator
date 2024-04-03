package com.gl.maker.model;

import lombok.Data;

@Data
public class DataModel {

    /**
     * 是否生成.gitignore文件
     */
    public boolean needGit;
    /**
     * 是否生成循环
     */
    public boolean loop;
    /**
     * 核心模板
     */
    public MyTemplate myTemplate;
}
