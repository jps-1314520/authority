package com.authority.service;

import com.authority.entity.UserRole;
import com.authority.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService extends ServiceImpl<UserRoleMapper, UserRole> {
}
