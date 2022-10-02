package com.roydon.reggie.controller;

import com.roydon.reggie.common.R;
import com.roydon.reggie.entity.Orders;
import com.roydon.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Intellij IDEA
 * Author: yi cheng
 * Date: 2022/10/2
 * Time: 17:03
 **/
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders order) {

        log.info("订单数据接收：{}",order);
        orderService.submit(order);

        return R.success("下单成功");

    }

}
