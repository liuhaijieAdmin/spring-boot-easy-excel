package com.zhuzi.service;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuzi.base.BaseImportExcelVO;
import com.zhuzi.base.CountVO;
import com.zhuzi.entity.Panda;
import com.zhuzi.enums.ResponseCode;
import com.zhuzi.enums.Sex;
import com.zhuzi.exception.BusinessException;
import com.zhuzi.listener.CommonListener;
import com.zhuzi.mapper.PandaMapper;
import com.zhuzi.model.bo.PandaStatisticsBO;
import com.zhuzi.model.dto.PandaQueryDTO;
import com.zhuzi.model.dto.PandaStatisticsDTO;
import com.zhuzi.model.excel.PandaReadErrorModel;
import com.zhuzi.model.excel.PandaReadModel;
import com.zhuzi.model.vo.*;
import com.zhuzi.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 熊猫表
 */
@Slf4j
@Service
public class PandaServiceImpl extends ServiceImpl<PandaMapper, Panda> implements PandaService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcelV1(MultipartFile file) {
        // 创建通用监听器来解析excel文件
        CommonListener<PandaReadModel> listener = new CommonListener<>(PandaReadModel.class);
        try {
            EasyExcelFactory.read(file.getInputStream(), PandaReadModel.class, listener).sheet().doRead();
        } catch (IOException e) {
            log.error("导入熊猫数据出错：{}: {}", e, e.getMessage());
            throw new BusinessException(ResponseCode.ANALYSIS_EXCEL_ERROR, "网络繁忙，请稍后重试！");
        }

        // 对读取到的数据进行批量保存
        List<PandaReadModel> excelData = listener.getData();
        batchSaveExcelData(excelData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveExcelData(List<PandaReadModel> excelData) {
        List<Panda> pandas = excelData.stream().map(model -> {
            Panda panda = new Panda();
            BeanUtils.copyProperties(model, panda, "sex");
            panda.setSex(Sex.codeOfValue(model.getSex()));
            panda.setCreateTime(new Date());
            return panda;
        }).collect(Collectors.toList());
        saveBatch(pandas);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseImportExcelVO<String> importExcelV2(MultipartFile file) {
        CommonListener<PandaReadModel> listener = new CommonListener<>(PandaReadModel.class);
        try {
            EasyExcelFactory.read(file.getInputStream(), PandaReadModel.class, listener).sheet().doRead();
        } catch (IOException e) {
            log.error("导入熊猫数据出错：{}: {}", e, e.getMessage());
            throw new BusinessException(ResponseCode.ANALYSIS_EXCEL_ERROR, "网络繁忙，请稍后重试！");
        }

        List<PandaReadModel> excelData = listener.getData();
        if (excelData.size() == 0) {
            throw new BusinessException(ResponseCode.ANALYSIS_EXCEL_ERROR, "请检查您上传的excel文件是否为空！");
        }

        // 校验excel数据，如果校验未通过直接阻断执行，将错误信息返回给调用方
        BaseImportExcelVO<String> result = validateExcelData(excelData);
        if (result.getErrorFlag()) {
            return result;
        }

        // 对校验通过的数据进行批量落库
        batchSaveExcelData(excelData);
        return result;
    }

    /*
    * 校验导入的excel数据
    * */
    private BaseImportExcelVO<String> validateExcelData(List<PandaReadModel> excelData) {
        BaseImportExcelVO<String> result = new BaseImportExcelVO<>();
        boolean errorFlag = false;
        List<PandaReadErrorModel> validatePandas = new ArrayList<>();
        String birthdayErrorMsg = "生日不能为空;", uniCodeErrorMsg = "唯一编码重复;";

        // 根据唯一编码查询库内的熊猫数
        List<String> uniCodes = excelData.stream().map(PandaReadModel::getUniqueCode).collect(Collectors.toList());
        List<CountVO<String, Integer>> counts = baseMapper.selectCountByUniCodes(uniCodes);
        Map<String, Integer> countMap = counts.stream().collect(Collectors.toMap(CountVO::getKey, CountVO::getValue));

        // 循环对excel所有数据行进行校验
        for (PandaReadModel excelRow : excelData) {
            String errorMsg = "";
            PandaReadErrorModel errorModel = new PandaReadErrorModel();
            BeanUtils.copyProperties(excelRow, errorModel);
            // 如果库里对应的唯一编码能查到熊猫，说明UniqueCode重复
            if (countMap.containsKey(excelRow.getUniqueCode())) {
                errorMsg += uniCodeErrorMsg;
                errorFlag = true;
            }
            // 如果导入的生日字段为空，说明对应的excel行没填写出生日期
            if (null == excelRow.getBirthday()) {
                errorMsg += birthdayErrorMsg;
                errorFlag = true;
            }
            errorModel.setErrorMsg(errorMsg);
            validatePandas.add(errorModel);
        }

        // 如果存在校验未通过的记录，则导出校验出错的数据为excel文件
        if (errorFlag) {
            String url, fileName = "熊猫信息导入-校验出错文件-" + System.currentTimeMillis();
            try {
                url = ExcelUtil.exportExcelToOSS(PandaReadErrorModel.class, validatePandas, fileName, ExcelTypeEnum.XLSX);
//                ExcelUtil.exportExcel(PandaReadErrorModel.class, validatePandas, fileName, ExcelTypeEnum.XLSX, response);
            } catch (IOException e) {
                log.error("生成熊猫导入校验出错文件失败：{}: {}", e, e.getMessage());
                throw new BusinessException(ResponseCode.ANALYSIS_EXCEL_ERROR, "网络繁忙，请稍后重试！");
            }
            result.setResult(url);
            result.setCheckMsg("文件校验未通过！");
        }
        result.setErrorFlag(errorFlag);
        return result;
    }

    @Override
    public void exportExcelByCondition(PandaQueryDTO queryDTO, HttpServletResponse response) {
        List<PandaExportVO> pandas = baseMapper.selectPandas(queryDTO);
        String fileName = "熊猫基本信息集合-" + System.currentTimeMillis();
        try {
            ExcelUtil.exportExcel(PandaExportVO.class, pandas, fileName, ExcelTypeEnum.XLSX, response);
        } catch (IOException e) {
            log.error("熊猫数据导出失败，{}：{}", e, e.getMessage());
            throw new BusinessException("熊猫基本信息导出失败，请稍后再试！");
        }
    }

    @Override
    public void exportExcelV2(PandaQueryDTO queryDTO, HttpServletResponse response) {
        List<PandaExportVO> pandas = baseMapper.selectPandas(queryDTO);

        // 求和所有熊猫的身高，并伪装成一条普通数据，加入到集合尾部
        BigDecimal heightSum = pandas.stream().map(PandaExportVO::getHeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        PandaExportVO sumVO = new PandaExportVO();
        sumVO.setUniqueCode("身高合计：");
        sumVO.setHeight(heightSum);
        pandas.add(sumVO);

        String fileName = "熊猫基本信息集合-" + System.currentTimeMillis();
        try {
            ExcelUtil.exportExcel(PandaExportVO.class, pandas, fileName, ExcelTypeEnum.XLSX, response);
        } catch (IOException e) {
            log.error("熊猫数据导出失败，{}：{}", e, e.getMessage());
            throw new BusinessException("熊猫基本信息导出失败，请稍后再试！");
        }
    }

    @Override
    public void exportStatisticsData(PandaStatisticsDTO statisticsDTO, HttpServletResponse response) {
        List<PandaStatisticsBO> pandaStatisticsBOs = baseMapper.selectPandaStatistics(statisticsDTO);

        List<PandaStatisticsExportVO> exportData;
        Class<?> clazz;
        // 如果是按性别分组统计，则使用性别的数据模型类
        if (0 == statisticsDTO.getStatisticsType()) {
            clazz = SexStatisticsExportVO.class;
            exportData = pandaStatisticsBOs.stream().map(bo -> {
                SexStatisticsExportVO sexVO = new SexStatisticsExportVO();
                BeanUtils.copyProperties(bo, sexVO);
                return sexVO;
            }).collect(Collectors.toList());
        }
        // 如果是按等级分组统计，则使用等级的数据模型类
        else if (1 == statisticsDTO.getStatisticsType()) {
            clazz = LevelStatisticsExportVO.class;
            exportData = pandaStatisticsBOs.stream().map(bo -> {
                LevelStatisticsExportVO levelVO = new LevelStatisticsExportVO();
                BeanUtils.copyProperties(bo, levelVO);
                return levelVO;
            }).collect(Collectors.toList());
        } else {
            throw new BusinessException("暂不支持这种统计方式哦~");
        }

        // 导出对应的excel数据
        String fileName = "熊猫统计数据-" + System.currentTimeMillis();
        try {
            ExcelUtil.exportExcel(clazz, exportData, fileName, ExcelTypeEnum.XLSX, response);
        } catch (IOException e) {
            log.error("熊猫统计数据导出失败，{}：{}", e, e.getMessage());
            throw new BusinessException("统计数据导出失败，请稍后再试！");
        }
    }

    @Override
    public void exportMultiLineHeadExcel(HttpServletResponse response) {
        List<MultiLineHeadExportVO> pandas = baseMapper.selectAllPandas();
        String fileName = "多行表头熊猫数据-" + System.currentTimeMillis();
        try {
            ExcelUtil.exportExcel(MultiLineHeadExportVO.class, pandas, fileName, ExcelTypeEnum.XLSX, response);
        } catch (IOException e) {
            log.error("多行表头熊猫数据，{}：{}", e, e.getMessage());
            throw new BusinessException("数据导出失败，请稍后再试！");
        }
    }
}
