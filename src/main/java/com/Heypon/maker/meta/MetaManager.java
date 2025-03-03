package com.Heypon.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

public class MetaManager {

    /**
     * volatile 关键字
     * 确保多线程环境下的内存可见性，确保 meta 对象被创建后，所有线程都可以看见
     * 防止重复初始化
     */
    private static volatile Meta meta;

    public static Meta getMetaObject(){
        /**
         * 双检锁单例模式
         * 防止重复初始化
         */
        if (meta == null){
            synchronized (MetaManager.class) {
                if (meta == null){
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("springboot-init-meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        // 检验和处理默认值
        MetaValidator.doValidAndFill(newMeta);
        return newMeta;
    }
}
