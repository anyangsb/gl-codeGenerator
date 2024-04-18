package com.gl.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;

@Data
public class GeneratorCacheRequest implements Serializable {

    /**
     * 生成器的 id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}