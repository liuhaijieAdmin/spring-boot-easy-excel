package com.zhuzi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuzi.entity.ExcelTask;
import com.zhuzi.enums.TaskHandleStatus;
import com.zhuzi.mapper.ExcelTaskMapper;
import org.springframework.stereotype.Service;

/**
 * 报表任务表
 */
@Service
public class ExcelTaskServiceImpl extends ServiceImpl<ExcelTaskMapper, ExcelTask> implements ExcelTaskService {

    @Override
    public void updateStatus(Long taskId, TaskHandleStatus status) {
        baseMapper.updateStatusById(taskId, status.getCode());
    }
}
