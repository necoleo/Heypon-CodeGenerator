package com.Heypon.web.mapper;

import com.Heypon.web.model.entity.Generator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 92700
 * @description 针对表【generator(代码生成器)】的数据库操作Mapper
 * @createDate 2025-03-07 14:33:51
 * @Entity generator.domain.Generator
 */
public interface GeneratorMapper extends BaseMapper<Generator> {
    @Select("SELECT id, distPath FROM generator WHERE isDelete = 1")
    List<Generator> listDeleteGenerator();
}




