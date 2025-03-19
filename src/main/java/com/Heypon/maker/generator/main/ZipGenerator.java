package com.Heypon.maker.generator.main;


/**
 * 生成代码生成器压缩包
 */
public class ZipGenerator extends GenerateTemplate {
    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputPath) {
        String distPath = super.buildDist(outputPath, sourceCopyDestPath, jarPath, shellOutputPath);
        return super.buildZip(distPath);
    }
}
