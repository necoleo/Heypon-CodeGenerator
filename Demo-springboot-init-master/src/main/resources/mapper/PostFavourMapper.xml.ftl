<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${basePackage}.springbootinit.mapper.PostFavourMapper">

    <resultMap id="BaseResultMap" type="${basePackage}.springbootinit.model.entity.PostFavour">
        <id property="id" column="id" jdbcType="BIGINT"/>
        <result property="postId" column="postId" jdbcType="BIGINT"/>
        <result property="userId" column="userId" jdbcType="BIGINT"/>
        <result property="createTime" column="createTime" jdbcType="TIMESTAMP"/>
        <result property="updateTime" column="updateTime" jdbcType="TIMESTAMP"/>
    </resultMap>

    <sql id="Base_Column_List">
        id,postId,userId,
        createTime,updateTime
    </sql>

    <select id="listFavourPostByPage"
            resultType="${basePackage}.springbootinit.model.entity.Post">
        select p.*
        from post p
                 join (select postId from post_favour where userId = <#noparse>#{favourUserId}</#noparse>) pf
                      on p.id = pf.postId <#noparse>${ew.customSqlSegment}</#noparse>
    </select>
</mapper>
