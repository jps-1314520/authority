package com.authority.service;

import com.authority.entity.Permission;
import com.authority.entity.Role;
import com.authority.entity.UserRole;
import com.authority.mapper.ModuleMapper;
import com.authority.mapper.PermissionMapper;
import com.authority.mapper.RoleMapper;
import com.authority.mapper.UserRoleMapper;
import com.authority.utils.AssertUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends ServiceImpl<RoleMapper,Role> {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private PermissionService permissionService;

    @Resource
    private ModuleMapper moduleMapper;

    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    public List<Role> selectAll(){
        return roleMapper.selectList(null);
    }

    public void saveRole(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空！");
        QueryWrapper<Role> wrapper = new QueryWrapper<Role>().eq("role_name", role.getRoleName());
        AssertUtil.isTrue(null != roleMapper.selectOne(wrapper),"该角色已存在!");
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.insert(role)<1,"角色记录添加失败！");
    }

    public  void updateRole(Role role){
        //参数校验
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"请输入角色名称!");
        //mybatis-plus的条件构造器（所有与role_name字段相等的项）
        QueryWrapper<Role> wrapper = new QueryWrapper<Role>().eq("role_name", role.getRoleName());
        //查询角色
        Role temp = roleMapper.selectOne(wrapper);
        //角色存在，但是传入的id与数据库中该角色的id值一致，则是已存在
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(role.getId())) ,"该角色已存在!");
        //传入更新时间
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.updateById(role) < 1,"角色记录更新失败!");
    }

    //删除角色
    public void deleteRole(Integer roleId){
        //查询角色是否存在
        Role role = roleMapper.selectById(roleId);
        AssertUtil.isTrue(null == role,"待删除的记录不存在!");

        //mybatis-plus的条件构造器（所有与role_id字段相等的项）
        QueryWrapper<UserRole> wrapper = new QueryWrapper<UserRole>().eq("role_id",roleId);
        int total =userRoleMapper.selectCount(wrapper);
        //如果关联表中中有记录，先删除中间表中的数据
        if(total > 0){
            //直接删除中间表t_user_role中的关联数据
            AssertUtil.isTrue(userRoleMapper.delete(wrapper) != total,"用户角色记录删除失败!");
        }
        //System.out.println(role);
        //使用mybatis-plus中的逻辑删除 is_valid = 0
        AssertUtil.isTrue(roleMapper.deleteById(roleId) < 1,"角色记录删除失败!");
    }

    public void addGrant(Integer[] mids, Integer roleId) {
        //mybatis-plus的条件构造器（所有与role_id字段相等的项）
        QueryWrapper<Permission> wrapper = new QueryWrapper<Permission>().eq("role_id",roleId);
        int total = permissionMapper.selectCount(wrapper);
        //如果关联表中中有记录，先删除中间表中的数据
        if(total > 0){
            AssertUtil.isTrue(permissionMapper.delete(wrapper) != total,"角色授权失败!");
        }

        //如果有传入权限值 则进行辅助操作
        if(null != mids && mids.length > 0){
            //接收所有权限对象
            List<Permission> permissions = new ArrayList<Permission>();
            for(Integer mid : mids){
                Permission permission = new Permission();
                permission.setCreateDate(new Date());
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setUpdateDate(new Date());
                //权限值
                permission.setAclValue(moduleMapper.selectById(mid).getOptValue());
                //添加到集合中
                permissions.add(permission);
            }
            //批量添加到权限表中
            AssertUtil.isTrue(!(permissionService.saveBatch(permissions)),"角色授权失败!");
        }
    }
}
