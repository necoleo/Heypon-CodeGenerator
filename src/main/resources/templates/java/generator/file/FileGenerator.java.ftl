package ${basePackage}.generator.file;

import cn.hutool.core.io.FileUtil;
import  ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class FileGenerator {
    /**
     * 主代码生成器
     * 分别调用静态生成器和动态生成器
     * @param model ftl数据模型配置
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws TemplateException, IOException {

        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
        <#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        System.out.println(inputPath);
        <#if fileInfo.generateType == "static">
        StaticFileGenerator.doStaticGenerate(inputPath, outputPath);
        </#if>
        <#if fileInfo.generateType == "dynamic">
        DynamicFileGenerator.doDynamicGenreate(inputPath, outputPath, model);
        </#if>

    </#list>
    }
}
