package com.authority.service;

import com.authority.entity.User;
import com.authority.entity.UserRole;
import com.authority.mapper.UserMapper;
import com.authority.mapper.UserRoleMapper;
import com.authority.utils.AssertUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper,User> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private UserRoleService userRoleService;

    public User userLogin(String userName,String userPwd){
       checkUserParams(userName,userPwd);
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user==null,"用户不存在！");
        AssertUtil.isTrue(!userPwd.equals(user.getUserPassword()),"用户密码错误！");
        return user;
    }

    private void checkUserParams(String userName, String userPwd) {
        //判断用户名
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        //判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空！");
    }

    public List<User> queryUserByParams(){
        return userMapper.selectList(null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user){
        checkFormParams(user.getUserName(),user.getEmail(),user.getPhone());
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPassword("123456");
        AssertUtil.isTrue(userMapper.insert(user)<1,"用户添加失败！");

        Integer userId = userMapper.queryUserByUserName(user.getUserName()).getId();
        String roleIds = user.getRoleIds();
        relationUserRoles(userId,roleIds);

    }

    private void checkFormParams(String userName, String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"请输入用户名!");
        AssertUtil.isTrue(StringUtils.isBlank(email),"请输入邮箱!");
        AssertUtil.isTrue(StringUtils.isBlank(phone),"请输入手机号码！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        checkFormParams(user.getUserName(),user.getEmail(),user.getPhone());
        User temp = userMapper.selectById(user.getId());
        AssertUtil.isTrue(null==temp,"待更新的用户记录不存在！");
        temp = userMapper.queryUserByUserName(user.getUserName());
        AssertUtil.isTrue(null != temp&&!(temp.getId().equals(user.getId())),"该用户已存在！");
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(userMapper.updateById(user)<1,"用户更新失败！");
        relationUserRoles(user.getId(), user.getRoleIds());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer[] ids) {
        AssertUtil.isTrue(null == ids||ids.length==0,"请选择待删除的用户记录！");
        AssertUtil.isTrue(userMapper.deleteBatchIds(Arrays.asList(ids))<ids.length,"用户记录删除失败！");
    }

    private void relationUserRoles(Integer userId, String roleIds) {

        //mybatis-plus的条件构造器（所有与user_id字段相等的项）
        QueryWrapper<UserRole> wrapper = new QueryWrapper<UserRole>().eq("user_id", userId);
        //查询角色表中，当前用户有多少个角色项
        int total = userRoleMapper.selectCount(wrapper);

        //如果大于0，则为更新操作，需要将之前的角色赋值删除，然后执行往后的角色赋值操作
        if(total>0){
            AssertUtil.isTrue(userRoleMapper.delete(wrapper) != total,"用户角色记录设置失败!");
        }
        //如果设置的角色不为空
        if(StringUtils.isNotBlank(roleIds)){
            //使用List存储每一个角色列信息，然后插入角色表中
            List<UserRole> userRoles = new ArrayList<UserRole>();
            //使用split进行字符串切割，每一次循环都得到一个角色项
            for(String role : roleIds.split(",")){
                //新建一个角色
                UserRole userRole=new UserRole();
                userRole.setCreateDate(new Date());
                //设置角色
                userRole.setRoleId(Integer.parseInt(role));
                userRole.setUpdateDate(new Date());
                //设置新建（更新）用户的id
                userRole.setUserId(userId);
                //放入到List
                userRoles.add(userRole);
            }
            //批量插入角色表
            AssertUtil.isTrue(!(userRoleService.saveBatch(userRoles)),"用户角色记录添加失败!");
        }
    }
}
