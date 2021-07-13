package com.authority.service;

import com.authority.entity.Module;
import com.authority.entity.Permission;
import com.authority.entity.ZTree;
import com.authority.mapper.ModuleMapper;
import com.authority.mapper.PermissionMapper;
import com.authority.utils.AssertUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class ModuleService extends ServiceImpl<ModuleMapper, Module> {
    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    //查询所有已经给传入角色分配的资源
    public List<ZTree> queryAllModules(Integer roleId){
        //先查询所有资源
        List<ZTree> ZTrees = moduleMapper.queryAllModules();
        // 查询角色已分配的菜单id
        List<Integer> mids = permissionMapper.queryRoleAllModIds(roleId);
        /**
         * 如果角色已经拥有了某个资源的权限，则将z-tree的checked值置为true，
         * 这样就能让前端页面显示的时候可以显示已经打勾
         */
        if(null != mids && mids.size() > 0){
            ZTrees.forEach( tree ->{
                //查看当前ZTree对象的id是否包含在 用户已经被分配到的资源id集合中
                if(mids.contains(tree.getId())){
                    // 设置为已经被选择，用作前台展示
                    tree.setChecked(true);
                }
            });
        }
        return ZTrees;
    }

    public List<Module> queryModules(){
        return moduleMapper.selectList(null);
    }

    public void saveModule(Module module){
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"菜单名不能为空！");
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade||!(grade==0||grade==1),"菜单层级非法！");
        Module module1 = moduleMapper.selectOne(new QueryWrapper<Module>().eq("grade", grade).eq("module_name",module.getModuleName()));
        AssertUtil.isTrue(null !=null,"该层级下菜单名已存在！");
        if (grade == 0){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请输入一级菜单url地址!");
            module1 = moduleMapper.selectOne(new QueryWrapper<Module>().eq("grade", grade).eq("url",module.getUrl()));
            AssertUtil.isTrue(null != module1,"一级级菜单下url不可重复!");
        }
        // 二级 必须指定上级菜单id
        if(grade != 0){
            AssertUtil.isTrue(null == module.getParentId() ||
                    //mybatis-plus的条件构造器（所有与id字段相等的项,即传入的父级菜单parent_id，可以在表中找到）
                    null == moduleMapper.selectOne(new QueryWrapper<Module>().eq("id", module.getParentId())),"请指定上级菜单!");
        }

        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入菜单权限码!");
        //查询特征码是否存在
        module1 = moduleMapper.selectOne(new QueryWrapper<Module>().eq("opt_value", module.getOptValue()));
        AssertUtil.isTrue(null !=module1,"权限码重复!");

        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        //添加数据
        AssertUtil.isTrue(moduleMapper.insert(module) < 1,"菜单添加失败!");
    }

    public void updateModule(Module module){
        Module temp = moduleMapper.selectById(module.getId());
        AssertUtil.isTrue(null == temp,"待修改的记录不存在！");
        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade||!(grade==0||grade==1),"菜单层级非法！");
        temp = moduleMapper.selectOne(new QueryWrapper<Module>().eq("grade", grade).eq("module_name",module.getModuleName()));
        AssertUtil.isTrue(null !=temp && !(temp.getId().equals(module.getId())),"该层级下菜单名已存在!");
        if(grade == 1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"请输入二级菜单url地址!");
            //mybatis-plus的条件构造器（所有与grade以及module.getUrl()字段相等的项）
            temp = moduleMapper.selectOne(new QueryWrapper<Module>().eq("grade", grade).eq("url",module.getUrl()));
            AssertUtil.isTrue(null != temp && !(temp.getId().equals(module.getId())),"二级菜单下url不可重复!");
        }

        // 二级 必须指定上级菜单id
        if(grade != 0){
            AssertUtil.isTrue(null==module.getParentId() ||
                    //mybatis-plus的条件构造器（所有与id字段相等的项,即传入的父级菜单parent_id，可以在表中找到）
                    null == moduleMapper.selectOne(new QueryWrapper<Module>().eq("id", module.getParentId())),"请指定上级菜单!");
        }

        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"请输入菜单权限码!");
        temp = moduleMapper.selectOne(new QueryWrapper<Module>().eq("opt_value", module.getOptValue()));
        AssertUtil.isTrue(null !=temp && !(temp.getId().equals(module.getId())),"权限码重复!");

        module.setUpdateDate(new Date());
        //更新数据
        AssertUtil.isTrue(moduleMapper.updateById(module) < 1,"菜单记录更新失败!");
    }

    public void deleteModule(Integer mid){
        /**
         * 1.记录必须存在
         *     id 非空  记录存在
         * 2.如果待删除的菜单存在子菜单  不允许直接删除当前菜单
         * 3.如果删除的菜单 在权限表中存在对应记录  此时要级联删除权限表对应记录
         */
        Module temp = moduleMapper.selectById(mid);
        AssertUtil.isTrue(null == temp,"待删除的记录不存在!");
        //通过mybatis-plus查询，需要验证选择菜单没有子菜单，则说明该菜单的id不存在于任何菜单的父级菜单中
        Integer total = moduleMapper.selectCount(new QueryWrapper<Module>().eq("parent_id", mid));
        AssertUtil.isTrue(total > 0,"存在子菜单，暂不支持删除操作!");

        // 删除权限表(permission表)对应记录
        Permission permission = permissionMapper.selectById(mid);
        //如果存在记录
        if(null != permission){
            AssertUtil.isTrue(permissionMapper.delete(new QueryWrapper<Permission>().eq("parent_id", mid)) < 1,"菜单记录删除失败!");
        }


        AssertUtil.isTrue(moduleMapper.deleteById(mid) < 1,"菜单记录删除失败!");
    }

}
