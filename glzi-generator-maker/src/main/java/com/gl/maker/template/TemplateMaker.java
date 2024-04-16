package com.gl.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.gl.maker.meta.Meta;
import com.gl.maker.meta.enums.FileGenerateTypeEnum;
import com.gl.maker.meta.enums.FileTypeEnum;
import com.gl.maker.template.enums.FileFilterRangeEnum;
import com.gl.maker.template.enums.FileFilterRuleEnum;
import com.gl.maker.template.model.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateMaker {

    public static void main(String[] args) {

        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        //指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "glzi-generator-demo-projects/glzi-generator-web-backend";
        String inputFilePath1 = "src/main/java/com/gl/springbootinit/common";
        String inputFilePath2 = "src/main/resources/application.yml";
        List<String> inputFilePathList = Arrays.asList(inputFilePath1,inputFilePath2);

        //模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        //模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
        //模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);
        //模型参数信息（首次）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum = ");


        //模型参数信息（第二次）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("className");
//        modelInfo.setType("String");
        //替换变量（首次）
//        String searchStr = "Sum: ";
        //第二次
        String searchStr = "BaseResponse";
        //文件过滤
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        fileFilterConfigList.add(fileFilterConfig);
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1,fileInfoConfig2));

        //分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

//        long id = makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,1775453119019745280L);
//        System.out.println(id);
    }

    /**
     * 制作模板
     * @param newMeta
     * @param originProjectPath
     * @param templateMakerFileConfig

     * @param id
     * @return
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath,
                                    TemplateMakerFileConfig templateMakerFileConfig
            , TemplateMakerModelConfig templateMakerModelConfig
            , TemplateMakerOutputConfig templateMakerOutputConfig, Long id){
        if(id == null){
            id = IdUtil.getSnowflakeNextId();
        }
        //复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator +id;
        //是否为首次制作模板
        //目录不存在。则是首次制作
        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath,templatePath,true);
        }

        //输入文件信息
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath),1,null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();
        //win 系统需要对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");
        //二.制作文件模板
        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);
        //处理模型信息
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);
        //三、生成配置文件
        String metaOutputPath = templatePath+File.separator+"meta.json";
        //如果已有meta文件说明不是第一次制作，在meta基础上进行修改
        if(FileUtil.exist(metaOutputPath)){
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta,oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;
            //1.追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);
            //配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        }else{
            //1.构造配置参数
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);
        }
        //2.额外的输出配置
        if(templateMakerOutputConfig!=null){
            //文件外层和分组去重
            if(templateMakerOutputConfig.isRemoveGroupFilesFromRoot()){
                List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
            }
        }
        //输出原文件信息
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta),metaOutputPath);
        return id;
    }

    /**
     * 重载方法
     * @param templateMakerConfig
     * @return
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig fileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig modelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();
        Long id = templateMakerConfig.getId();
        return makeTemplate(meta,originProjectPath,fileConfig,modelConfig,outputConfig,id);
    }
    /**
     * 模型去重
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        //策略：同分组内模型merge,不同分组保留
        //1.有分组的，以分组为单位划分
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey)
                );
        //2.同组内的模型配置合并
        //保存每个组对应合并后的对象Map

        Map<String,Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for(Map.Entry<String,List<Meta.ModelConfig.ModelInfo>> entry:groupKeyModelInfoListMap.entrySet()){
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(
                            Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                    ).values());
            //使用新的group配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey,newModelInfo);
        }
        //3.将文件分组添加到结果列表
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());
        //4.将未分组文件添加到结果列表
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,o->o,(e,r)->r)
                ).values()));
        return  resultList;
    }

    /**
     * 文件去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        //策略：同分组内文件merge,不同分组保留
        //1.有分组的，以组为单位划分
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        //2.同组内的文件配置合并
        //保存每组对应的合并后的对象Map
        Map<String,Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for(Map.Entry<String,List<Meta.FileConfig.FileInfo>> entry:groupKeyFileInfoListMap.entrySet()){
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                    ).values());
            //使用新的group配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey,newFileInfo);
        }
        //3.将文件分组添加到结果列表
        ArrayList<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());
        //4.将未分组文件添加到结果列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath,o->o,(e,r)->r)
                ).values()));
        return  resultList;
    }

    private static Meta.FileConfig.FileInfo makeFileTemplate(
            TemplateMakerModelConfig templateMakerModelConfig,
            String sourceRootPath,File inputFile,
            TemplateMakerFileConfig.FileInfoConfig fileInfoConfig){
        //要挖坑的文件的绝对路径
        //win系统需要对路径进行转义
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");
        String fileOutputAbsolutePath = fileInputAbsolutePath+".ftl";
        //文件输入输出相对路径

        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/","");
        String fileOutputPath = fileInputPath + ".ftl";
//二、使用字符串替换，生成模板文件
        String fileContent = null;


        //如果已有模板文件，说明不是第一次制作，则在模板基础上再次挖坑
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        //支持多个模型：对同一个文件的内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String replacement;
        String newFileContent = fileContent;
        for(TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig:templateMakerModelConfig.getModels()){
            //不是分组
            if(modelGroupConfig == null){
                replacement = String.format("${%s}",modelInfoConfig.getFieldName());
            }else {
                //是分组
                String groupKey = modelGroupConfig.getGroupKey();
                //多挖一个层级
                replacement = String.format("${%s.%s}",groupKey,modelInfoConfig.getFieldName());
            }
            //多次替换
            newFileContent = StrUtil.replace(newFileContent,modelInfoConfig.getReplaceText(),replacement);
        }
        //文件信息配置
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        //是否更改了文件内容
        boolean contentEquals = newFileContent.equals(fileContent);
        //之前不存在模板文件，并且没有更改文件内容，则为静态生成
        if(!hasTemplateFile){
            if(contentEquals){
                //输出路径等于输入路径
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            }else {
                //生成模板文件
                FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
            }
        }
        return fileInfo;
    }

    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig , TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath){
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        //非空校验
        if(templateMakerFileConfig == null){
            return newFileInfoList;
        }
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        if(CollUtil.isEmpty(fileConfigInfoList)){
            return newFileInfoList;
        }
        //生成模板文件，遍历输入文件
        for(TemplateMakerFileConfig.FileInfoConfig fileInfoConfig: fileConfigInfoList){
            String inputFilePath = fileInfoConfig.getPath();
            //若填的是相对路径，改为绝对路径
            if(!inputFilePath.startsWith(sourceRootPath)){
                inputFilePath = sourceRootPath + File.separator +inputFilePath;
            }
            //获取过滤后的文件列表
            List<File> fileList = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFilterConfigList());
            //不处理已经生成的FTL模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());
            for(File file :fileList){
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig,sourceRootPath,file,fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        //如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if(fileGroupConfig!=null){
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            //新增配置分组
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            //文件全放到一个分组里
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }

    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig){
        //本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        if(templateMakerModelConfig == null){
            return newModelInfoList;
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if(CollUtil.isEmpty(models)){
            return newModelInfoList;
        }
        //处理模型信息
        //转化为配置接受的ModelInfo对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());
        //如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if(modelGroupConfig!=null){
            //新增配置分组
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig,groupModelInfo);
            //模型全放到一个分组里
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        }else {
            //不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }
}
