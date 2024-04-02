package com.gl.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.gl.maker.generator.JarGenerator;
import com.gl.maker.generator.ScriptGenerator;
import com.gl.maker.generator.file.DynamicFileGenerator;
import com.gl.maker.meta.Meta;
import com.gl.maker.meta.MetaManager;
import com.gl.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator extends GenerateTemplate{

    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputFilePath) {
        System.out.println("不需要输出精简版了");
    }
}