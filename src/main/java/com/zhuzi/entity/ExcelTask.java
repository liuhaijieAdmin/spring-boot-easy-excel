package com.zhuzi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 报表任务表
 */
@Data
public class ExcelTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
	@TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    /**
     * 任务类型，0：导出，1：导入
     */
	@TableField("task_type")
    private Integer taskType;

    /**
     * 处理状态，0：待处理，1：处理中，2：成功，3：失败
     */
	@TableField("handle_status")
    private Integer handleStatus;

    /**
     * excel链接
     */
	@TableField("excel_url")
    private String excelUrl;

    /**
     * 链路ID
     */
	@TableField("trace_id")
    private String traceId;

    /**
     * 请求参数
     */
	@TableField("request_params")
    private String requestParams;

    /**
     * 异常类型
     */
	@TableField("exception_type")
    private String exceptionType;

    /**
     * 异常描述
     */
	@TableField("error_msg")
    private String errorMsg;

	/*
	* 创建时间
	* */
	@TableField("create_time")
    private Date createTime;

    /*
     * 修改时间
     * */
    @TableField("update_time")
    private Date updateTime;
}
