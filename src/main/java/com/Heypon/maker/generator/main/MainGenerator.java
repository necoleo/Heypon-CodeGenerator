package com.Heypon.maker.generator.main;

public class MainGenerator extends GenerateTemplate{

    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputPath) {
        System.out.println("dont do buildDist");
        return "";
    }
}
