package com.roydon.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roydon.reggie.common.BaseContext;
import com.roydon.reggie.common.R;
import com.roydon.reggie.entity.ShoppingCart;
import com.roydon.reggie.service.DishFlavorService;
import com.roydon.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Intellij IDEA
 * Author: yi cheng
 * Date: 2022/10/2
 * Time: 15:59
 **/
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService ShoppingCartService;

    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @param session
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {

        log.info("添加购物车：{}", shoppingCart);

        Long userId = (Long) session.getAttribute("user");
        log.info("点餐用户id：{}", userId);
        shoppingCart.setUserId(userId);

        // 添加购物车之前，要确定是否已经在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

//        DishFlavor dishFlavor = new DishFlavor();

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            // 添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
//            dishFlavor.setDishId(shoppingCart.getDishId());
        } else {
            // 添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
//            dishFlavor.setDishId(shoppingCart.getSetmealId());
        }
        ShoppingCart one = ShoppingCartService.getOne(queryWrapper);
        if (one != null) {
            // 已经在购物车中，在原来的基础上加1
            one.setNumber(one.getNumber() + 1);
//            one.setAmount(shoppingCart.getAmount().multiply(new BigDecimal(one.getNumber())));
            ShoppingCartService.updateById(one);
        } else {
            // 新增
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            ShoppingCartService.save(shoppingCart);
            one = shoppingCart;
//            dishFlavor.setValue(shoppingCart.getDishFlavor());
//            dishFlavorService.save(dishFlavor);
        }

        return R.success(one);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session) {

        Long userId = (Long) session.getAttribute("user");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        return R.success(ShoppingCartService.list(queryWrapper));
    }

    /**
     * 减单购物车
     *
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {

        log.info("购物车减单post数据：{}", shoppingCart);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        if (shoppingCart.getDishId() != null) {
            // 此单为菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            // 此单为套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cart = ShoppingCartService.getOne(queryWrapper);
        // 数量大于一时
        if (cart.getNumber() > 1) {
            cart.setNumber(cart.getNumber() - 1);
            ShoppingCartService.updateById(cart);
//            cart=shoppingCart;
        } else {
            ShoppingCartService.remove(queryWrapper);
        }

        // TODO 有bug，减一时不刷新
        return R.success("减一");
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        ShoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
