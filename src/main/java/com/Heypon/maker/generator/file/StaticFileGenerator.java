package com.Heypon.maker.generator.file;

import cn.hutool.core.io.FileUtil;

import java.io.File;

public class StaticFileGenerator {
    public static void main(String[] args){
        String projectPath = System.getProperty("user.dir");
        String inputPath = projectPath + File.separator + "Demo-Projects";
        String outputPath = projectPath + File.separator + "test";
        doStaticGenerate(inputPath, outputPath);
    }

    /**
     * 静态生成
     * 无需修改文件的任何参数
     * 使用 Hutool 工具类的 copy 方法复制文件夹及其文件
     * @param inputPath 输入路径，即目标文件路径
     * @param outputPath 输出路径，即文件保存路径
     */
    public static void doStaticGenerate(String inputPath, String outputPath){
        FileUtil.copy(inputPath, outputPath, false);
    }
}
