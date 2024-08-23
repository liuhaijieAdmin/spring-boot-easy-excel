package com.zhuzi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuzi.entity.ExcelTask;
import com.zhuzi.enums.TaskHandleStatus;

/**
 * 报表任务表
 */
public interface ExcelTaskService extends IService<ExcelTask> {
    /*
    * 更新任务状态
    * */
    void updateStatus(Long taskId, TaskHandleStatus status);
}
