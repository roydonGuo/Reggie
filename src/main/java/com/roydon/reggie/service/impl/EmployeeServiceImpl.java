package com.roydon.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roydon.reggie.entity.Employee;
import com.roydon.reggie.mapper.EmployeeMapper;
import com.roydon.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


}

