package com.authority.controller;

import com.authority.entity.ResultInfo;
import com.authority.entity.User;
import com.authority.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "用户管理模块")
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("用户登录")
    @GetMapping("login")
    public ResultInfo userLogin(String userName,String userPassword){
        ResultInfo resultInfo = new ResultInfo();
        User user = userService.userLogin(userName, userPassword);
        resultInfo.setResult(user);
        return resultInfo;
    }

    @ApiOperation("查询所有的用户")
    @GetMapping("list")
    public List<User> queryUserByParams(){
        return userService.queryUserByParams();
    }

    @ApiOperation("添加用户")
    @PostMapping("save")
    public ResultInfo saveUser(@RequestBody User user){
        userService.saveUser(user);
        return new ResultInfo();
    }

    @ApiOperation("修改用户信息")
    @PostMapping("update")
    public ResultInfo updateUser(@RequestBody User user){
        userService.updateUser(user);
        return new ResultInfo();
    }

    @ApiOperation("删除用户")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteUser(ids);
        return new ResultInfo();
    }
}
