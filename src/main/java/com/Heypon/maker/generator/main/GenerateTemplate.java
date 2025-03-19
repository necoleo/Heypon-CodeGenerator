package com.Heypon.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.Heypon.maker.generator.JarGenerator;
import com.Heypon.maker.generator.ScriptGenerator;
import com.Heypon.maker.generator.file.DynamicFileGenerator;
import com.Heypon.maker.meta.Meta;
import com.Heypon.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public abstract class GenerateTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        doGenerate(meta, outputPath);
    }

    /**
     * 生成
     * @param meta
     * @param outputPath
     * @throws TemplateException
     * @throws IOException
     * @throws InterruptedException
     */
    public void doGenerate(Meta meta, String outputPath) throws TemplateException, IOException, InterruptedException {
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 1.复制原始文件
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 2.代码生成
        generateCode(meta, outputPath);

        // 3.构建 jar 包
        String jarPath = buildJar(outputPath, meta);

        // 4.封装脚本
        String shellOutPath = getBuildScript(outputPath, jarPath);

        // 5.生成精简版的程序(产物包)
        buildDist(outputPath, sourceCopyDestPath, jarPath, shellOutPath);
    }

    /**
     * 复制原始项目文件
     * @param meta
     * @param outputPath
     * @return
     */
    protected String copySource(Meta meta, String outputPath) {
        // 从原始模板文件路径source复制到生成的代码包中
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath,sourceCopyDestPath,false);
        return sourceCopyDestPath;
    }

    /**
     * 代码生成
     * @param meta
     * @param outputPath
     * @throws IOException
     * @throws TemplateException
     */
    protected void generateCode(Meta meta, String outputPath) throws IOException, TemplateException {

        String inputResourcePath = "";

        // Java 包的基础路径
        String outputBasePackage = meta.getBasePackage();
        // 上面导出来的路径格式是 com.xxx，将路径格式转换为 com/xxx
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage,"."));
        // generated/src/main/java/com/xxx
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // 生成 model.DataModel
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成 cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成 cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成 cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成 cli.command.JsonGenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/JsonGenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/JsonGenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成 cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成 Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成 file.FileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/file/FileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/file/FileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // 生成 file.DynamicFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/file/DynamicFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/file/DynamicFileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // 生成 file.StaticFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/file/StaticFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/file/StaticFileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // 生成 pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + "/pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

    }

    /**
     * 构建 jar 包
     * @param outputPath
     * @param meta
     * @return 返回 jar 包的相对路径
     * @throws InterruptedException
     * @throws IOException
     */
    protected String buildJar(String outputPath, Meta meta) throws InterruptedException, IOException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        return jarPath;
    }

    /**
     * 构建脚本
     * @param outputPath
     * @param jarPath
     * @return
     */
    protected String getBuildScript(String outputPath, String jarPath) {
        String shellOutputPath = outputPath + File.separator + "generator";
        ScriptGenerator.doGenerate(shellOutputPath,jarPath);
        return shellOutputPath;
    }

    /**
     * 生成精简版程序
     * @param outputPath
     * @param sourceCopyDestPath
     * @param jarPath
     * @param shellOutputPath
     * @return 产物包路径
     */
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputPath) {
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
        return distOutputPath;
    }




    /**
     * 制作压缩包
     * @param outputPath
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    protected String buildZip(String outputPath){
        String zipPath = outputPath + ".zip";
        ZipUtil.zip(outputPath, zipPath);
        return zipPath;
    }

}