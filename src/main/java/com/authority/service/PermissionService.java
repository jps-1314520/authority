package com.authority.service;

import com.authority.entity.Permission;
import com.authority.mapper.PermissionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {
    @Resource
    private PermissionMapper permissionMapper;

    //通过传入用户id查询该用户被授权的资源
    public List<String> queryUserHasRoleIdsHasModuleIds(Integer userId) {
        return  permissionMapper.queryUserHasRoleIdsHasModuleIds(userId);
    }
}
