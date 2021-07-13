package com.authority.controller;

import com.authority.entity.ResultInfo;
import com.authority.entity.Role;
import com.authority.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "角色管理模块")
@RestController
@RequestMapping("role")
public class RoleController {
    @Resource
    private RoleService roleService;

    @ApiOperation("通过用户id查询角色")
    @GetMapping("queryAllRoles")
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    @ApiOperation("查询所有的角色")
    @GetMapping("list")
    public List<Role> selectAll(){
        return roleService.selectAll();
    }

    @ApiOperation("添加角色")
    @PostMapping("save")
    public ResultInfo saveRole(@RequestBody Role role){
        roleService.saveRole(role);
        return new ResultInfo();
    }

    @ApiOperation("更新角色")
    @PostMapping("update")
    public ResultInfo updateRole(@RequestBody Role role){
        roleService.updateRole(role);
        return new ResultInfo();
    }

    @ApiOperation("删除角色")
    @PostMapping("delete")
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return new ResultInfo();
    }

    @ApiOperation("给角色赋予权限")
    @PostMapping("addGrant")
    public ResultInfo addGrant(Integer[] ids,Integer roleId){
        roleService.addGrant(ids, roleId);
        return new ResultInfo();
    }
}
