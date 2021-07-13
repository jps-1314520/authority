package com.authority.mapper;

import com.authority.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role> {
    List<Map<String, Object>> queryAllRoles(Integer userId);
}
