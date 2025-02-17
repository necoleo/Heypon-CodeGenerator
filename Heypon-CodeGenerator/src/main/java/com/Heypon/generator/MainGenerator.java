package com.Heypon.generator;

import com.Heypon.model.MainTemplateModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        String projectPath = System.getProperty("user.dir");
        String StaticinputPath = projectPath + File.separator + "Demo-Projects";
        String StaticoutputPath = projectPath + File.separator + "test";
        String DynamicinputPath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "templateFtl" + File.separator;
        String DynamicOutputPath = StaticoutputPath + File.separator + "Demo-Projects" + File.separator + "src" + File.separator + "java" + File.separator + "com" + File.separator + "demo" + File.separator + "MainTemplate.java";

        MainTemplateModel mainTemplateModel = new MainTemplateModel();
        mainTemplateModel.setAuthor("Heypon");
        mainTemplateModel.setOutputText("I am a hero");
        mainTemplateModel.setLoop(true);

        doGenerate(StaticinputPath, StaticoutputPath,DynamicinputPath, DynamicOutputPath, mainTemplateModel);
    }

    /**
     * 主代码生成器
     * 分别调用静态生成器和动态生成器
     * @param StaticInputPath 静态文件路径
     * @param StaticOutputPath 静态文件保存路径
     * @param DynamicInputPath  ftl模板文件保存路径
     * @param DynamicOutputPath 动态文件保存路径
     * @param model ftl数据模型配置
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(String StaticInputPath,String StaticOutputPath, String DynamicInputPath, String DynamicOutputPath, Object model) throws TemplateException, IOException {

        StaticGenerator.doStaticGenerate(StaticInputPath, StaticOutputPath);
        DynamicGenerator.doDynamicGenreate(DynamicInputPath, DynamicOutputPath, model);
    }
}
