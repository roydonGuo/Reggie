package com.roydon.reggie.entity.dto;

import com.roydon.reggie.entity.Setmeal;
import com.roydon.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
