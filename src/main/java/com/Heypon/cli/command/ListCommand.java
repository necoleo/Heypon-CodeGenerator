package com.Heypon.cli.command;


import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.Collections;
import java.util.List;

@CommandLine.Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{
    @Override
    public void run(){
        System.out.println("查看文件列表");
        String projectPath = System.getProperty("user.dir");
        // 获取静态文件（项目源代码位置）
        String StaticInputPath = projectPath + File.separator + "Demo-Projects";

        // 调用Hutool工具类中的 loopFiles 方法获取目录下的所有文件列表
        List<File> files = FileUtil.loopFiles(StaticInputPath);
        // 反转文件列表
        Collections.reverse(files);
        for (File file : files) {
            System.out.println(file);
        }
    }
}
