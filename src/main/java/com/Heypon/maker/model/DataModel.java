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
     * 作者名称
     */
    private String author = "Heypon";

    /**
     * 输出信息
     */
    private String outputText = "sum";

    /**
     * 是否循环
     */
    private boolean loop;
}
