package com.wen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户信息 Mapper
 * @Author : 青灯文案
 * @Date: 2026/3/14
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
