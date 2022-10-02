package com.roydon.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roydon.reggie.common.BaseContext;
import com.roydon.reggie.entity.*;
import com.roydon.reggie.exception.CustomException;
import com.roydon.reggie.mapper.OrderMapper;
import com.roydon.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by Intellij IDEA
 * Author: yi cheng
 * Date: 2022/10/2
 * Time: 16:57
 **/
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {


    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private UserService userService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {

        //TODO 1、获取用户id
        Long userId = BaseContext.getCurrentId();

        //TODO 2、查询用户购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，不可下单");
        }
        // 查询用户
        User user = userService.getById(userId);
        // 用户地址
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("地址信息有误");
        }

        //TODO 3、向订单order表插入数据一条
        Long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(orderId));// 设置订单号
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);

        //TODO 4、向订单详情orderDetail表插入数据，可能多条
        orderDetailService.saveBatch(orderDetails);
        //TODO 5、下单完成，清空购物车
        shoppingCartService.remove(queryWrapper);

    }
}
