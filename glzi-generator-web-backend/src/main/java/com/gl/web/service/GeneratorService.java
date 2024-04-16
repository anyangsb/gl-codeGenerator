package com.gl.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gl.web.model.dto.generator.GeneratorQueryRequest;
import com.gl.web.model.entity.Generator;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gl.web.model.vo.GeneratorVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 19328
* @description 针对表【generator(代码生成器)】的数据库操作Service
* @createDate 2024-04-15 18:24:57
*/
public interface GeneratorService extends IService<Generator> {

    /**
     * 校验
     *
     * @param generator
     * @param add
     */
    void validGenerator(Generator generator, boolean add);

    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest
     * @return
     */
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param generator
     * @param request
     * @return
     */
    GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param generatorPage
     * @param request
     * @return
     */
    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request);
}
