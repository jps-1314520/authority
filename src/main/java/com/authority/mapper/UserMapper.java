package com.authority.mapper;

import com.authority.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface UserMapper extends BaseMapper<User>{
    //通过名字查询用户
    User  queryUserByUserName(String userName);
}
