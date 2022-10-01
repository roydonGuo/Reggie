package com.roydon.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roydon.reggie.entity.Setmeal;
import com.roydon.reggie.entity.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     *
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
