package com.gl.generator;

import com.gl.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainGenerator {
    public static void doGenerate(Object model) throws IOException, TemplateException {
        String inputRootPath = "D:\\SpringBoot2\\代码生成器\\glzi-generator\\glzi-generator-basic\\acm-template";
        String outputRootPath = "D:\\SpringBoot2\\代码生成器\\glzi-generator\\acm-template";

        String inputPath;
        String outputPath;

        inputPath = new File(inputRootPath,"src/com/yupi/acm/")
    }

}
