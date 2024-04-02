package com.gl.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class StaticFileGenerator {

    /**
     * 拷贝文件
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByHutool(String inputPath, String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }


    public static void copyFilesByRecursive(String inputPath,String outputPath){
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            copyFileByRecursive(inputFile,outputFile);
        } catch (IOException e) {
            System.out.println("复制文件失败");
            e.printStackTrace();
        }
    }

    private static void copyFileByRecursive(File inputFile,File outputFile) throws IOException {
        //区分是目录还是文件
        if(inputFile.isDirectory()){
            System.out.println(inputFile.getName());
            File destOutputFile = new File(outputFile, inputFile.getName());
            //如果是目录，首先创建目标目录
            if(!destOutputFile.exists()){
                destOutputFile.mkdir();
            }
            //获取目录下所有子目录和文件
            File[] files = inputFile.listFiles();
            //无文件直接结束
            if(ArrayUtil.isEmpty(files)){
                return;
            }
            for(File file : files){
                //递归拷贝下一层文件
                copyFileByRecursive(file,destOutputFile);
            }
        }else{
            //是文件，直接复制到目标文件下
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(),destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
