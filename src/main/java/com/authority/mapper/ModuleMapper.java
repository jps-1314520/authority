package com.authority.mapper;

import com.authority.entity.Module;
import com.authority.entity.ZTree;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module> {
    List<ZTree> queryAllModules();
}
