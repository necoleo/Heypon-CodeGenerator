package com.Heypon.maker.model;

import lombok.Data;

/**
 * 动态模板配置
 */
@Data
public class DataModel {
    /*
        1. 在代码开头增加作者 @Author 注释 （增加代码）
        2. 修改程序输出的信息提示 （替换代码）
        3. 将循环读取输入改为单次读取 （可选代码）
    */

    /**
     * 是否生成 .gitignore 文件
     */
    private boolean needGit = true;

    /**
     * 是否循环
     */
    private boolean loop = false;

    /**
     * 核心模板
     */
    public MainTemplate mainTemplate = new MainTemplate();

    @Data
    public class MainTemplate {
        /**
         * 作者名称
         */
        private String author = "Heypon";

        /**
         * 输出信息
         */
        private String outputText = "sum";

    }

}
