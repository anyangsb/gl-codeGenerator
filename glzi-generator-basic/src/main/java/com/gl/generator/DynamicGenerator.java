package com.gl.generator;

import com.gl.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DynamicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        String inputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "MainTemplate/java";
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("gl");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("求和结果：");
        doGenerate(inputPath,outputPath,mainTemplateConfig);
    }

    public static void doGenerate(String inputPath,String outputPath,Object model) throws IOException, TemplateException {
        //new 个对象，参数为freemarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        //设置文件所在路径和字符集
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);
        configuration.setDefaultEncoding("utf-8");
        //创建模板对象
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        //生成
        FileWriter out = new FileWriter(outputPath);
        template.process(model,out);
        //关闭输出流
        out.close();
    }
}
