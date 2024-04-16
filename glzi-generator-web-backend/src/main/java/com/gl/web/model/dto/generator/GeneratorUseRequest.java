package com.gl.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class GeneratorUseRequest implements Serializable {

    /**
     * 生成器ID
     */
    private Long id;

    /**
     * 数据模型
     */
    Map<String , Object> dataModel;

    private static final long serialVersionUID = 1L;
}
