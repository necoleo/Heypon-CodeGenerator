package com.Heypon;

import com.Heypon.maker.generator.main.GenerateTemplate;
import com.Heypon.maker.generator.main.MainGenerator;
import com.Heypon.maker.generator.main.ZipGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
//        GenerateTemplate generateTemplate = new MainGenerator();
        GenerateTemplate generateTemplate = new ZipGenerator();
        generateTemplate.doGenerate();
    }
}