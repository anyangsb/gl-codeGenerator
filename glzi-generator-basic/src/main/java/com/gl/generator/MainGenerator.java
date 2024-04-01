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
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        //输入路径
        String inputPath = new File(parentFile, "acm-template").getAbsolutePath();
//        System.out.println(inputPath);
        //输出路径
        String outputPath = projectPath;
        StaticGenerator.copyFilesByHutool(inputPath,outputPath);
        //生成动态文件
        String inputDynamicFilePath= projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputDynamicFilePath= outputPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
        DynamicGenerator.doGenerate(inputDynamicFilePath,outputDynamicFilePath,model);
    }

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("gl");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("求和结果：");
        doGenerate(mainTemplateConfig);
    }
}
