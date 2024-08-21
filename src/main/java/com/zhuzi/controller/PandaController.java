package com.zhuzi.controller;

import com.zhuzi.base.BaseImportExcelVO;
import com.zhuzi.base.ServerResponse;
import com.zhuzi.enums.ResponseCode;
import com.zhuzi.exception.BusinessException;
import com.zhuzi.model.dto.PandaQueryDTO;
import com.zhuzi.model.dto.PandaStatisticsDTO;
import com.zhuzi.service.PandaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 熊猫接口
 */
@RestController
@RequestMapping("/panda")
public class PandaController {

    @Resource
    private PandaService pandaService;

    @PostMapping("/import/v1")
    public ServerResponse<Void> importExcelV1(MultipartFile file) {
        if (null == file) {
            throw new BusinessException(ResponseCode.FILE_IS_NULL);
        }
        pandaService.importExcelV1(file);
        return ServerResponse.success();
    }

    @PostMapping("/import/v2")
    public ServerResponse<BaseImportExcelVO<String>> importExcelV2(MultipartFile file) {
        if (null == file) {
            throw new BusinessException(ResponseCode.FILE_IS_NULL);
        }
        return ServerResponse.success(pandaService.importExcelV2(file));
    }

    @PostMapping("/export/v1")
    public void exportExcelV1(@RequestBody PandaQueryDTO queryDTO, HttpServletResponse response) {
        pandaService.exportExcelByCondition(queryDTO, response);
    }

    @PostMapping("/export/v2")
    public void exportExcelV2(@RequestBody PandaQueryDTO queryDTO, HttpServletResponse response) {
        pandaService.exportExcelV2(queryDTO, response);
    }

    @PostMapping("/export/v3")
    public void exportExcelV2(@RequestBody PandaStatisticsDTO statisticsDTO, HttpServletResponse response) {
        pandaService.exportStatisticsData(statisticsDTO, response);
    }

    @PostMapping("/export/v4")
    public void exportExcelV2(HttpServletResponse response) {
        pandaService.exportMultiLineHeadExcel(response);
    }

}
