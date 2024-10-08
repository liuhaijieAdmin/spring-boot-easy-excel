package com.zhuzi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuzi.base.BaseImportExcelVO;
import com.zhuzi.entity.Panda;
import com.zhuzi.model.dto.PandaQueryDTO;
import com.zhuzi.model.dto.PandaStatisticsDTO;
import com.zhuzi.model.excel.Panda1mReadModel;
import com.zhuzi.model.excel.PandaReadModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 熊猫表
 */
public interface PandaService extends IService<Panda> {

	/*
	* 基于excel文件导入熊猫数据并落库存储
	* */
	void importExcelV1(MultipartFile file);

	/*
	* 批量保存导入的excel数据
	* */
	void batchSaveExcelData(List<PandaReadModel> excelData);

	/*
	* 导入excel数据并校验数据，通过则落库，否则返回校验出错的excel文件
	* */
	BaseImportExcelVO<String> importExcelV2(MultipartFile file);


	/*
	* 根据搜索条件导出excel数据
	* */
	void exportExcelByCondition(PandaQueryDTO queryDTO, HttpServletResponse response);

	/*
	* 根据条件导出并求和身高
	* */
	void exportExcelV2(PandaQueryDTO queryDTO, HttpServletResponse response);

	/*
	* 导出熊猫统计数据
	* */
	void exportStatisticsData(PandaStatisticsDTO statisticsDTO, HttpServletResponse response);

	/*
	* 导出多行头的excel文件
	* */
	void exportMultiLineHeadExcel(HttpServletResponse response);

	/*
	* 导出百万量级的excel文件
	* */
	void export1mPandaExcel(HttpServletResponse response);

	/*
	 * 导出百万量级的excel文件（优化版）
	 * */
	Long export1mPandaExcelV2();

	/*
	* 导入100W数据的excel文件（通用监听器）
	* */
    void import1MExcelV1(MultipartFile file);

	/*
	* 导入100W数据的excel文件（分批处理监听器）
	* */
	void import1MExcelV2(MultipartFile file);

	/*
	* 批量保存熊猫导入数据
	* */
	void batchSavePandas(List<Panda1mReadModel> excelData);

	/*
	* 导入100W数据的excel报表
	* */
	Long import1MExcelV3(MultipartFile file);

	/*
	* 导出熊猫统计数据（复杂报表）
	* */
    void exportPandaStatisticsData(HttpServletResponse response);
}
