package com.Heypon.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.Heypon.maker.meta.Meta;
import com.Heypon.maker.template.model.TemplateMakerConfig;
import com.Heypon.maker.template.model.TemplateMakerFileConfig;
import com.Heypon.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;
import org.junit.runner.Request;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TemplateMakerTest {

//    @Test
//    public void makeTemplate() {
//    }
    /**
     * 使用 JSON 制作模板
     */
    @Test
    public void testMakeTemplateWithJSON() {
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 测试同配置多次生成时，强制变为静态生成
     */
    @Test
    public void testMakeTemplateBug1() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "Demo-CodeGenerator-springboot/Demo-springboot-init-master";

        // 文件参数配置
        String inputFilePath1 = "src/main/resources/application.yml";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig,null,  1735281524670181376L);
        System.out.println(id);
    }

    /**
     * 同文件目录多次生成时，会扫描新的 FTL 文件
     */
    @Test
    public void testMakeTemplateBug2() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "Demo-CodeGenerator-springboot/Demo-springboot-init-master";

        // 文件参数配置，扫描目录
        String inputFilePath1 = "src/main/java/com/yupi/web/common";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, null, 1895319639513432064L);
        System.out.println(id);
    }

    /**
     * 制作 SpringBoot 模板
     */
    @Test
    public void makeSpringBootTemplate() {
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        Long id = TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker1.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker2.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        System.out.println(templateMakerConfig);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker3.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker4.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker5.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker6.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker7.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateMaker8.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);


        System.out.println(id);
    }

}