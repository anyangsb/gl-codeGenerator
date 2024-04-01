package com.gl.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name="list",description = "查看文件列表",mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{

    @Override
    public void run() {
        String property = System.getProperty("user.dir");
        //整个项目的根目录
        File parentFile = new File(property).getParentFile();

        String inputPath = new File(parentFile, "acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);
        for(File file:files){
            System.out.println(file);
        }
    }
}
