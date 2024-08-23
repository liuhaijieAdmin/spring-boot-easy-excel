package com.zhuzi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuzi.entity.ExcelTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 报表任务表
 */
@Mapper
@Repository
public interface ExcelTaskMapper extends BaseMapper<ExcelTask> {

    void updateStatusById(@Param("taskId") Long taskId, @Param("status") Integer status);

}
