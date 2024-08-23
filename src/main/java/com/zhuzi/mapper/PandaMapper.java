package com.zhuzi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuzi.base.CountVO;
import com.zhuzi.entity.Panda;
import com.zhuzi.model.bo.PandaStatisticsBO;
import com.zhuzi.model.dto.PandaQueryDTO;
import com.zhuzi.model.dto.PandaStatisticsDTO;
import com.zhuzi.model.vo.MultiLineHeadExportVO;
import com.zhuzi.model.vo.Panda1mExportVO;
import com.zhuzi.model.vo.PandaExportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 熊猫表
 */
@Mapper
@Repository
public interface PandaMapper extends BaseMapper<Panda> {
    List<PandaExportVO> selectPandas(@Param("params") PandaQueryDTO queryDTO);

    /*
    * 根据唯一编码查询数量
    * */
    List<CountVO<String, Integer>> selectCountByUniCodes(@Param("uniCodes") List<String> uniCodes);

    List<PandaStatisticsBO> selectPandaStatistics(@Param("params") PandaStatisticsDTO statisticsDTO);

    List<MultiLineHeadExportVO> selectAllPandas();

    List<Panda1mExportVO> select1mPandas();

    List<Panda1mExportVO> selectPandaPage(@Param("startId") Long startId, @Param("rows") Integer rows);

    int selectTotalRows();
}
