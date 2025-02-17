package com.Heypon.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.Heypon.generator.MainGenerator;
import com.Heypon.model.MainTemplateModel;
import lombok.Data;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable {

    /**
     * 作者（字符串，填充值）
     */
    @CommandLine.Option(names = {"-a", "--author"}, arity = "0..1", description = "作者", interactive = true, echo = true)
    private String author;

    /**
     * 输出信息
     */
    @CommandLine.Option(names = {"-o", "--outputText"}, arity = "0..1", description = "输出信息", interactive = true, echo = true)
    private String outputText;

    /**
     * 是否循环（开关）
     */
    @CommandLine.Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否循环", interactive = true, echo = true)
    private boolean loop;

    @Override
    public Integer call() throws Exception{
        MainTemplateModel mainTemplateModel = new MainTemplateModel();
        BeanUtil.copyProperties(this, mainTemplateModel);
        String projectPath = System.getProperty("user.dir");
        String StaticinputPath = projectPath + File.separator + "Demo-Projects";
        String StaticoutputPath = projectPath + File.separator + "Demo-Project-Test";
        String DynamicinputPath = projectPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "templateFtl" + File.separator;
        String DynamicOutputPath = StaticoutputPath + File.separator + "Demo-Projects" + File.separator + "src" + File.separator + "java" + File.separator + "com" + File.separator + "demo" + File.separator + "MainTemplate.java";

        MainGenerator.doGenerate(StaticinputPath, StaticoutputPath, DynamicinputPath, DynamicOutputPath, mainTemplateModel);
        return 0;
    }
}
