package ${basePackage}.generator.file;

import cn.hutool.core.io.FileUtil;
import  ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "static">
${indent}StaticFileGenerator.doStaticGenerate(inputPath, outputPath);
</#if>
<#if fileInfo.generateType == "dynamic">
${indent}DynamicFileGenerator.doDynamicGenreate(inputPath, outputPath, model);
</#if>
</#macro>

/**
 * 主代码生成器
 */
public class FileGenerator {
    /**
     * 分别调用静态生成器和动态生成器
     * @param model ftl数据模型配置
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String outputText = model.mainTemplate.outputText;
        String author = model.mainTemplate.author;

        String inputPath;
        String outputPath;

    <#-- 获取模型变量 -->
    <#list modelConfig.models as modelInfo>
        <#-- 有分组 -->
        <#if modelInfo.groupKey??>
        <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
    </#list>

    <#list fileConfig.files as fileInfo>
        <#if fileInfo.groupKey??>
        // groupKey = ${fileInfo.groupKey}
        <#if fileInfo.condition??>
        if (${fileInfo.condition}){
           <#list fileInfo.files as fileInfo>
           <@generateFile fileInfo=fileInfo indent="           " />
           </#list>
        }
        <#else>
        <#list fileInfo.files as fileInfo>
        <@generateFile fileInfo=fileInfo indent="       " />
        </#list>
        </#if>
        <#else>
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            <@generateFile fileInfo=fileInfo indent="           " />
        }
        <#else>
        <@generateFile fileInfo=fileInfo indent="       " />
        </#if>
        </#if>
    </#list>
    }
}
