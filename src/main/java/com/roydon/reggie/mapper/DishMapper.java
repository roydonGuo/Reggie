package com.roydon.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roydon.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
