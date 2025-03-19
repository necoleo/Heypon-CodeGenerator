package com.Heypon.backend.model.dto.generator;


import com.Heypon.backend.meta.Meta;
import lombok.Data;

import java.io.Serializable;

@Data
public class GeneratorMakeRequest implements Serializable {

    /**
     * 元信息
     */
    private Meta meta;

    /**
     * 模板文件压缩包路径
     */
    private String zipFilePath;

    private static final long serialVersionUID = -7736071574471032871L;

}
