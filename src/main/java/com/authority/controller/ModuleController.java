package com.authority.controller;

import com.authority.entity.Module;
import com.authority.entity.ResultInfo;
import com.authority.entity.ZTree;
import com.authority.service.ModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "菜单模块")
@RestController
@RequestMapping("module")
public class ModuleController{
    @Resource
    private ModuleService moduleService;

    @ApiOperation("通过角色id查询权限")
    @PostMapping("queryAllModules")
    public List<ZTree> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    @ApiOperation("查询所有的菜单")
    @GetMapping("list")
    public List<Module> queryModules(){
        return moduleService.queryModules();
    }

    @ApiOperation("添加菜单")
    @PostMapping("save")
    public ResultInfo saveModule(@RequestBody Module module){
        moduleService.saveModule(module);
        return new ResultInfo();
    }

    @ApiOperation("修改菜单")
    @PostMapping("update")
    public ResultInfo updateModule(@RequestBody Module module){
        moduleService.updateModule(module);
        return new ResultInfo();
    }

    @ApiOperation("删除菜单")
    @PostMapping("delete")
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return new ResultInfo();
    }
}
