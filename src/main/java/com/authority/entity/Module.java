package com.authority.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Module {
    @ApiModelProperty(value = "资源id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "资源名")
    private String moduleName;

    @ApiModelProperty(value = "资源地址")
    private String url;

    @ApiModelProperty(value = "上级资源id")
    private Integer parentId;

    @ApiModelProperty(value = "层级")
    private Integer grade;

    @ApiModelProperty(value = "权限值")
    private String optValue;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createDate;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateDate;
}
