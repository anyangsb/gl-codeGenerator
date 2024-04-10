package com.gl.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.gl.maker.template.enums.FileFilterRangeEnum;
import com.gl.maker.template.enums.FileFilterRuleEnum;
import com.gl.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static com.gl.maker.template.enums.FileFilterRangeEnum.FILE_CONTENT;
import static com.gl.maker.template.enums.FileFilterRangeEnum.FILE_NAME;

public class FileFilter {

    /**
     * 单个文件过滤
     * @param filterConfigList 过滤规则
     * @param file 单个文件
     * @return 是否保留
     */
    public static boolean doSingleFileFilter(List<FileFilterConfig> filterConfigList, File file){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);
        //所有过滤器校验结束的结果
        boolean result = true;

        if(CollUtil.isEmpty(filterConfigList)){
            return  true;
        }

        for(FileFilterConfig fileFilterConfig : filterConfigList){
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if(fileFilterRangeEnum == null){
                continue;
            }
            //要过滤的原内容
            String content = fileName;
            switch (fileFilterRangeEnum){
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }
            FileFilterRuleEnum filterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if(filterRuleEnum == null){
                continue;
            }
            switch (filterRuleEnum){
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                default:
            }
            //有不满足则直接返回
            if(!result){
                return false;
            }
        }
        return true;
    }

    public static List<File> doFilter(String filePath,List<FileFilterConfig> fileFilterConfigList){
        //根据路径获取所有文件
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()
                .filter(file -> doSingleFileFilter(fileFilterConfigList,file))
                .collect(Collectors.toList());
    }
}
