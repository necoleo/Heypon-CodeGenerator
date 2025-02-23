package com.Heypon.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.Heypon.maker.generator.file.DynamicFileGenerator;
import com.Heypon.maker.meta.Meta;
import com.Heypon.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        // 获取 meta.json 的数据
        Meta meta = MetaManager.getMetaObject();

        // 输出的根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 从原始模板文件路径source复制到生成的代码包中
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath,sourceCopyDestPath,false);

        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // Java 包的基础路径
        String outputBasePackage = meta.getBasePackage();
        // 上面导出来的路径格式是 com.xxx，将路径格式转换为 com/xxx
        String outputBasePackagePath = StrUtil.join(File.separator, StrUtil.split(outputBasePackage,"."));
        // generated/src/main/java/com/xxx
        String outputBaseJavaPackagePath = outputPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // 生成 model.DataModel
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "model" +
                                            File.separator + "DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "model" + File.separator + "DataModel.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "cli" +
                                            File.separator + "command" +
                                            File.separator + "ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command" + File.separator + "ConfigCommand.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "cli" +
                                            File.separator + "command" +
                                            File.separator + "GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command" + File.separator + "GenerateCommand.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "cli" +
                                            File.separator + "command" +
                                            File.separator + "ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command" + File.separator + "ListCommand.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "cli" +
                                            File.separator + "CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "CommandExecutor.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 Main
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 file.FileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "generator" +
                                            File.separator + "file" +
                                            File.separator + "FileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "generator" + File.separator + "file" + File.separator + "FileGenerator.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 file.DynamicFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "generator" +
                                            File.separator + "file" +
                                            File.separator + "DynamicFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "generator" + File.separator + "file" + File.separator + "DynamicFileGenerator.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 file.StaticFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "java" +
                                            File.separator + "generator" +
                                            File.separator + "file" +
                                            File.separator + "StaticFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "generator" + File.separator + "file" + File.separator + "StaticFileGenerator.java";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 生成 README.md
        inputFilePath = inputResourcePath + File.separator + "templates" +
                                            File.separator + "README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doDynamicGenreate(inputFilePath,outputFilePath, meta);

        // 构建 jar 包
        JarGenerator.doGenerate(outputPath);

        // 封装脚本
        String shellOutputPath = outputPath + File.separator + "generator";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target" + File.separator + jarName;
        ScriptGenerator.doGenerate(shellOutputPath,jarPath);

        // 生成精简版的程序(产物包)
        String distOutputPath = outputPath + "-dist";
        // 拷贝 jar 包
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath,targetAbsolutePath,true);
        // 拷贝脚本文件
        FileUtil.copy(shellOutputPath,distOutputPath,true);
        FileUtil.copy(shellOutputPath + ".bat", distOutputPath,true);
        // 拷贝模板文件
        FileUtil.copy(sourceCopyDestPath,distOutputPath,true);
    }
}
