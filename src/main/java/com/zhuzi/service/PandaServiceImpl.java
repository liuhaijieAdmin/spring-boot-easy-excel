package com.zhuzi.service;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuzi.base.BaseImportExcelVO;
import com.zhuzi.base.CountVO;
import com.zhuzi.config.TaskThreadPool;
import com.zhuzi.entity.ExcelTask;
import com.zhuzi.entity.Panda;
import com.zhuzi.enums.*;
import com.zhuzi.exception.BusinessException;
import com.zhuzi.listener.BatchHandleListener;
import com.zhuzi.listener.ParallelBatchHandleListener;
import com.zhuzi.listener.CommonListener;
import com.zhuzi.mapper.PandaMapper;
import com.zhuzi.model.bo.PandaStatisticsBO;
import com.zhuzi.model.dto.PandaQueryDTO;
import com.zhuzi.model.dto.PandaStatisticsDTO;
import com.zhuzi.model.excel.Panda1mReadModel;
import com.zhuzi.model.excel.PandaReadErrorModel;
import com.zhuzi.model.excel.PandaReadModel;
import com.zhuzi.model.vo.*;
import com.zhuzi.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 熊猫表
 */
@Slf4j
@Service
public class PandaServiceImpl extends ServiceImpl<PandaMapper, Panda> implements PandaService {

    @Resource
    private ExcelTaskService excelTaskService;
    @Resource
    private MapperFacade mapperFacade;

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
            log.error("多行表头熊猫数据导出出错，{}：{}", e, e.getMessage());
            throw new BusinessException("数据导出失败，请稍后再试！");
        }
    }

    @Override
    public void export1mPandaExcel(HttpServletResponse response) {
        List<Panda1mExportVO> pandas = baseMapper.select1mPandas();
        String fileName = "百万级熊猫数据-" + System.currentTimeMillis();
        try {
            ExcelUtil.exportExcel(Panda1mExportVO.class, pandas, fileName, ExcelTypeEnum.XLSX, response);
        } catch (IOException e) {
            log.error("百万级熊猫数据导出出错，{}：{}", e, e.getMessage());
            throw new BusinessException("数据导出失败，请稍后再试！");
        }
    }

    @Override
    public Long export1mPandaExcelV2() {
        // 先插入一条报表任务记录
        ExcelTask excelTask = new ExcelTask();
        excelTask.setTaskType(ExcelTaskType.EXPORT.getCode());
        excelTask.setHandleStatus(TaskHandleStatus.WAIT_HANDLE.getCode());
        excelTask.setCreateTime(new Date());
        excelTaskService.save(excelTask);
        Long taskId = excelTask.getTaskId();

        // 将报表导出任务提交给异步线程池
        ThreadPoolTaskExecutor asyncPool = TaskThreadPool.getAsyncThreadPool();

        // 必须用try包裹，因为线程池已满时任务被拒绝会抛出异常
        try {
            asyncPool.submit(() -> {
                handleExportTask(taskId);
            });
        } catch (RejectedExecutionException e) {
            // 记录等待恢复的状态
            log.error("递交异步导出任务被拒，线程池任务已满，任务ID：{}", taskId);
            ExcelTask editTask = new ExcelTask();
            editTask.setTaskId(taskId);
            editTask.setHandleStatus(TaskHandleStatus.WAIT_TO_RESTORE.getCode());
            editTask.setExceptionType("异步线程池任务已满");
            editTask.setErrorMsg("等待重新载入线程池被调度！");
            editTask.setUpdateTime(new Date());
            excelTaskService.updateById(editTask);
        }

        return taskId;
    }

    /*
    * 处理报表导出任务
    * */
    private void handleExportTask(Long taskId) {
        long startTime = System.currentTimeMillis();
        log.info("处理报表导出任务开始，编号：{}，时间戳：{}", taskId, startTime);
        // 开始执行时，先将状态推进到进行中
        excelTaskService.updateStatus(taskId, TaskHandleStatus.IN_PROGRESS);

        // 需要修改的报表对象
        ExcelTask editTask = new ExcelTask();
        editTask.setTaskId(taskId);

        // 查询导出的总行数，如果为0，说明没有数据要导出，直接将任务推进到失败状态
        int totalRows = baseMapper.selectTotalRows();
        if (totalRows == 0) {
            editTask.setHandleStatus(TaskHandleStatus.FAILED.getCode());
            editTask.setExceptionType("数据为空");
            editTask.setErrorMsg("对应导出任务没有数据可导出！");
            editTask.setUpdateTime(new Date());
            excelTaskService.updateById(editTask);
            return;
        }

        // 总数除以每批数量，并向上取整得到批次数
        int batchRows = 2000;
        int batchNum = totalRows / batchRows + (totalRows % batchRows == 0 ? 0 : 1);
        // 总批次数除以并发比例，并向上取整得到并发轮数
        int concurrentRound = batchNum / TaskThreadPool.concurrentRate
                + (batchNum % TaskThreadPool.concurrentRate == 0 ? 0 : 1);;

        log.info("本次报表导出任务-目标数据量：{}条，每批数量：{}，总批次数：{}，并发总轮数：{}", totalRows, batchRows, batchNum, concurrentRound);

        // 提前创建excel写入对象（这里可以替换成上传至文件服务器）
        String fileName = "百万级熊猫数据-" + startTime + ".csv";
        ExcelWriter excelWriter = EasyExcelFactory.write(fileName, Panda1mExportVO.class)
                .excelType(ExcelTypeEnum.CSV)
                .build();
        // CSV文件这行其实可以不需要，设置了也不会生效
        WriteSheet writeSheet = EasyExcelFactory.writerSheet(0, "百万熊猫数据").build();

        // 根据计算出的并发轮数开始并发读取表内数据处理
        AtomicInteger cursor = new AtomicInteger(0);
        ThreadPoolTaskExecutor concurrentPool = TaskThreadPool.getConcurrentThreadPool();
        for (int i = 1; i <= concurrentRound; i++) {
            CountDownLatch countDownLatch = new CountDownLatch(TaskThreadPool.concurrentRate);
            final CopyOnWriteArrayList<Panda1mExportVO> data = new CopyOnWriteArrayList<>();
            for (int j = 0; j < TaskThreadPool.concurrentRate; j++) {
                final int startId = cursor.get() * batchRows + 1;
//                    log.info("当前批次：{}，起始ID：{}，线程池可用线程数：{}", cursor.get(), startId, concurrentPool.getActiveCount());
                concurrentPool.submit(() -> {
                    List<Panda1mExportVO> pandas = baseMapper.selectPandaPage((long) startId, batchRows);
                    if (null != pandas && pandas.size() != 0) {
                        data.addAll(pandas);
                    }
                    countDownLatch.countDown();
                });
                cursor.incrementAndGet();
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                editTask.setHandleStatus(TaskHandleStatus.FAILED.getCode());
                editTask.setExceptionType("导出等待中断");
                editTask.setErrorMsg(e.getMessage());
                editTask.setUpdateTime(new Date());
                excelTaskService.updateById(editTask);
                return;
            }
            excelWriter.write(data, writeSheet);
            // 手动清理每一轮的集合数据，用于辅助GC
            data.clear();
        }

        log.info("处理报表导出任务结束，编号：{}，导出耗时（ms）：{}", taskId, System.currentTimeMillis() - startTime);
        // 完成写入后，主动关闭资源
        excelWriter.finish();

        // 如果执行到最后，说明excel导出成功，将状态推进到导出成功
        editTask.setHandleStatus(TaskHandleStatus.SUCCEED.getCode());
        editTask.setExcelUrl(fileName);
        editTask.setUpdateTime(new Date());
        excelTaskService.updateById(editTask);
    }

    @Override
    public void import1MExcelV1(MultipartFile file) {
        CommonListener<Panda1mReadModel> listener = new CommonListener<>(Panda1mReadModel.class);
        try {
            EasyExcelFactory.read(file.getInputStream(), Panda1mReadModel.class, listener).sheet(0).doRead();
        } catch (IOException e) {
            log.error("导入百万熊猫数据出错：{}: {}", e, e.getMessage());
            throw new BusinessException(ResponseCode.ANALYSIS_EXCEL_ERROR, "网络繁忙，请稍后重试！");
        }
        List<Panda1mReadModel> data = listener.getData();
        log.info("导入百万级熊猫数据，成功解析到：{}条！", data.size());
    }

    @Override
    public void import1MExcelV2(MultipartFile file) {
        BatchHandleListener<Panda1mReadModel> listener = new BatchHandleListener<>(Panda1mReadModel.class, this::batchSavePandas);
        try {
            EasyExcelFactory.read(file.getInputStream(), Panda1mReadModel.class, listener).sheet(0).doRead();
        } catch (IOException e) {
            log.error("导入百万熊猫数据出错：{}: {}", e, e.getMessage());
            throw new BusinessException(ResponseCode.ANALYSIS_EXCEL_ERROR, "网络繁忙，请稍后重试！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSavePandas(List<Panda1mReadModel> excelData) {
        // 这里可以先实现数据行校验、业务有效性校验等逻辑，清洗后再将数据入库
        List<Panda> pandas = mapperFacade.mapAsList(excelData, Panda.class);
        this.saveBatch(pandas);
        pandas.clear();
    }

    @Override
    public Long import1MExcelV3(MultipartFile file) {
        // 先插入一条报表导入的任务记录
        ExcelTask excelTask = new ExcelTask();
        excelTask.setTaskType(ExcelTaskType.IMPORT.getCode());
        excelTask.setHandleStatus(TaskHandleStatus.WAIT_HANDLE.getCode());
        excelTask.setExcelUrl("实际请将传入的excel链接存入该字段");
        excelTask.setCreateTime(new Date());
        excelTaskService.save(excelTask);
        Long taskId = excelTask.getTaskId();

        // 将报表导入任务提交给异步线程池
        ThreadPoolTaskExecutor asyncPool = TaskThreadPool.getAsyncThreadPool();
        // 必须用try包裹，因为线程池已满时任务被拒绝会抛出异常
        try {
            asyncPool.submit(() -> {
                handleImportTask(taskId, file);
            });
        } catch (RejectedExecutionException e) {
            // 记录等待恢复的状态
            log.error("递交异步导入任务被拒，线程池任务已满，任务ID：{}", taskId);
            ExcelTask editTask = new ExcelTask();
            editTask.setTaskId(taskId);
            editTask.setHandleStatus(TaskHandleStatus.WAIT_TO_RESTORE.getCode());
            editTask.setErrorMsg("等待重新载入线程池被调度！");
            editTask.setExceptionType("异步线程池任务已满");
            editTask.setUpdateTime(new Date());
            excelTaskService.updateById(editTask);
        }

        return taskId;
    }

    @Override
    public void exportPandaStatisticsData(HttpServletResponse response) {
        // 查询熊猫集合数据
        List<Panda> pandas = baseMapper.selectAll();


        // 初始化各统计项变量
        String statisticsUser = "竹子爱熊猫";
        Date statisticsDate = new Date();
        int maleNumber=0, femaleNumber=0, unknownNumber=0, totalNumber=0,
                weekIncreaseNumber=0, monthIncreaseNumber=0, yearIncreaseNumber=0;
        BigDecimal avgHeight, maxHeight=null, minHeight=null, sumHeight=BigDecimal.ZERO;

        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;
        int currentWeek = today.get(Calendar.WEEK_OF_YEAR);

        for (Panda panda : pandas) {
            // 统计各项性别数据
            Sex sex = Sex.ofCode(panda.getSex());
            switch (sex) {
                case MALE:
                    maleNumber++;
                    break;
                case FEMALE:
                    femaleNumber++;
                    break;
                default:
                    unknownNumber++;
                    break;
            }

            // 统计各项身高数据
            BigDecimal height = panda.getHeight();
            sumHeight = sumHeight.add(height);
            if (null == maxHeight) {
                maxHeight = height;
            }
            if (null == minHeight) {
                minHeight = height;
            }

            if (minHeight.compareTo(height) < 0) {
                minHeight = height;
            }
            if (maxHeight.compareTo(height) > 0) {
                maxHeight = height;
            }

            // 统计本年、本月、本周新增人数
            Calendar createCal = Calendar.getInstance();
            createCal.setTime(panda.getCreateTime());
            int year = createCal.get(Calendar.YEAR);
            int month = createCal.get(Calendar.MONTH) + 1;
            int week = createCal.get(Calendar.WEEK_OF_YEAR);
            if (year == currentYear) {
                yearIncreaseNumber++;
                if (month == currentMonth) {
                    monthIncreaseNumber++;
                    if (week == currentWeek) {
                        weekIncreaseNumber++;
                    }
                }
            }
        }
        totalNumber = pandas.size();
        avgHeight = sumHeight.divide(new BigDecimal(totalNumber), RoundingMode.HALF_UP);

        Map<String, Object> dataMap = new HashMap<>(13);
        dataMap.put("statisticsUser", statisticsUser);
        dataMap.put("statisticsDate", statisticsDate);
        dataMap.put("maleNumber", maleNumber);
        dataMap.put("femaleNumber", femaleNumber);
        dataMap.put("unknownNumber", unknownNumber);
        dataMap.put("weekIncreaseNumber", weekIncreaseNumber);
        dataMap.put("monthIncreaseNumber", monthIncreaseNumber);
        dataMap.put("yearIncreaseNumber", yearIncreaseNumber);
        dataMap.put("avgHeight", avgHeight);
        dataMap.put("maxHeight", maxHeight);
        dataMap.put("minHeight", minHeight);
        dataMap.put("totalNumber", totalNumber);
        dataMap.put("illustrate", "以上数据来自熊猫表");

        String fileName = "熊猫数据统计报表";
        try {
            // 获取一个已设置基础参数的ExcelWriter对象
            ExcelWriter excelWriter = ExcelUtil.initExportFillWriter(fileName,
                    ExcelTypeEnum.XLSX, ExcelTemplate.PANDA_STATISTICS, response);
            WriteSheet writeSheet = EasyExcelFactory.writerSheet().build();
            // 基于模板填充数据
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(pandas, fillConfig, writeSheet);
            excelWriter.fill(dataMap, writeSheet);
            excelWriter.finish();
        }
        catch (IOException e) {
            throw new BusinessException("熊猫统计报表导出失败！");
        }
    }

    /*
    * 处理报表导入任务
    *   说明：实际场景不需要传File，而是基于taskId获取文件链接解析
    * */
    private void handleImportTask(Long taskId, MultipartFile file) {
        long startTime = System.currentTimeMillis();
        log.info("处理报表导入任务开始，编号：{}，时间戳：{}", taskId, startTime);
        excelTaskService.updateStatus(taskId, TaskHandleStatus.IN_PROGRESS);

        ExcelTask editTask = new ExcelTask();
        editTask.setTaskId(taskId);

        ParallelBatchHandleListener<Panda1mReadModel> listener =
                new ParallelBatchHandleListener<>(Panda1mReadModel.class, this::concurrentHandlePandas);
        listener.initThreshold(5);
        try {
            EasyExcelFactory.read(file.getInputStream(), Panda1mReadModel.class, listener).sheet(0).doRead();
        } catch (IOException e) {
            log.error("导入百万熊猫数据出错：{}: {}", e, e.getMessage());
            editTask.setHandleStatus(TaskHandleStatus.FAILED.getCode());
            editTask.setExceptionType("导入时获取文件流出错");
            editTask.setErrorMsg(e.getMessage());
            editTask.setUpdateTime(new Date());
            excelTaskService.updateById(editTask);
            return;
        }
        log.info("处理报表导入任务结束，编号：{}，导出耗时（ms）：{}", taskId, System.currentTimeMillis() - startTime);

        // 如果执行到最后，说明excel导出成功，将状态推进到导出成功
        editTask.setHandleStatus(TaskHandleStatus.SUCCEED.getCode());
        editTask.setUpdateTime(new Date());
        excelTaskService.updateById(editTask);
    }

    /*
    * 并发处理解析到的熊猫数据
    * */
    @SuppressWarnings("all")
    private void concurrentHandlePandas(List<Panda1mReadModel> excelData, ParallelBatchHandleListener<Panda1mReadModel> listener) {
        ThreadPoolTaskExecutor concurrentPool = TaskThreadPool.getConcurrentThreadPool();
        concurrentPool.submit(() -> {
            // 这里可以对数据行进行业务规则校验、清洗加工处理等逻辑处理
            List<Panda> pandas = mapperFacade.mapAsList(excelData, Panda.class);
            this.saveBatch(pandas);
            pandas = null;
            // 释放占用的许可
            listener.getConcurrentThreshold().release();
        });
    }

}
