package com.authority.mapper;

import com.authority.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    //通过传入角色id查询该角色被授权的资源
    public List<Integer> queryRoleAllModIds(Integer roleId);

    //通过用户id查询该用户被授权得资源
    public List<String> queryUserHasRoleIdsHasModuleIds(Integer userId);
}
