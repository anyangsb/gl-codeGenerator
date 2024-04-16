package com.gl.maker;

import com.gl.maker.generator.main.GenerateTemplate;
import com.gl.maker.generator.main.MainGenerator;
import com.gl.maker.generator.main.ZipGenerator;
import com.gl.maker.model.DataModel;
import freemarker.template.TemplateException;

import javax.xml.crypto.Data;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        GenerateTemplate generateTemplate = new ZipGenerator();
        generateTemplate.doGenerate();
    }
}
