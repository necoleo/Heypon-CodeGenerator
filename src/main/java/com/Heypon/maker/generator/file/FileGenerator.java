package com.Heypon.maker.generator.file;

import com.Heypon.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class FileGenerator {
    public static void main(String[] args) throws TemplateException, IOException {


        DataModel dataModel = new DataModel();
        dataModel.setAuthor("Heypon");
        dataModel.setOutputText("I am a hero");
        dataModel.setLoop(true);

        doGenerate(dataModel);
    }

    /**
     * 主代码生成器
     * 分别调用静态生成器和动态生成器
     * @param model ftl数据模型配置
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object model) throws TemplateException, IOException {

        String projectPath = System.getProperty("user.dir");
        String StaticInputPath = new File(projectPath, "Demo-Projects").getAbsolutePath();
        String StaticOutputPath = new File(projectPath, "test").getAbsolutePath();
        String DynamicInputPath =new File(projectPath, File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "templates" + File.separator).getAbsolutePath();
        String DynamicOutputPath =new File(StaticOutputPath, File.separator + "Demo-Projects" + File.separator + "src" + File.separator + "java" + File.separator + "com" + File.separator + "demo" + File.separator + "MainTemplate.java").getAbsolutePath();

        StaticFileGenerator.doStaticGenerate(StaticInputPath, StaticOutputPath);
        DynamicFileGenerator.doDynamicGenreate(DynamicInputPath, DynamicOutputPath, model);
    }
}
