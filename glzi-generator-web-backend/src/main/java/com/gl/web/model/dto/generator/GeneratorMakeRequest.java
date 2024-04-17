package com.gl.web.model.dto.generator;

import com.gl.maker.meta.Meta;
import lombok.Data;

import java.io.Serializable;

@Data
public class GeneratorMakeRequest implements Serializable {
    /**
     * 压缩文件路径
     */
    private String zipFilePath;

    /**
     * 元信息
     */
    private Meta meta;

    private static final long serialVersionUID = 1L;
}
