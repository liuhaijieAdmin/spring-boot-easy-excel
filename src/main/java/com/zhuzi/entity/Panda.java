package com.zhuzi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 熊猫表
 */
@Data
public class Panda implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 唯一编码
     */
    private String uniqueCode;

    /**
     * 性别，0：男，1：女，2：未知
     */
    private Integer sex;

    /**
     * 身高
     */
    private BigDecimal height;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 头像地址
     */
    private String pic;

    /**
     * 等级
     */
    private String level;

    /**
     * 座右铭
     */
    private String motto;

    /**
     * 所在地址
     */
    private String address;

	/*
	* 创建时间
	* */
	private Date createTime;

    /**
     * 删除标识，0：正常，1：删除
     */
    private Integer isDeleted;
}
