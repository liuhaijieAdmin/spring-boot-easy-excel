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

    @PostMapping("/import/v3")
    public ServerResponse<Void> importExcelV3(MultipartFile file) {
        if (null == file) {
            throw new BusinessException(ResponseCode.FILE_IS_NULL);
        }
        pandaService.import1MExcelV1(file);
        return ServerResponse.success();
    }

    @PostMapping("/import/v4")
    public ServerResponse<Void> importExcelV4(MultipartFile file) {
        if (null == file) {
            throw new BusinessException(ResponseCode.FILE_IS_NULL);
        }
        pandaService.import1MExcelV2(file);
        return ServerResponse.success();
    }

    @PostMapping("/import/v5")
    public ServerResponse<Long> importExcelV5(MultipartFile file) {
        if (null == file) {
            throw new BusinessException(ResponseCode.FILE_IS_NULL);
        }
        return ServerResponse.success(pandaService.import1MExcelV3(file));
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
    public void exportExcelV3(@RequestBody PandaStatisticsDTO statisticsDTO, HttpServletResponse response) {
        pandaService.exportStatisticsData(statisticsDTO, response);
    }

    @PostMapping("/export/v4")
    public void exportExcelV4(HttpServletResponse response) {
        pandaService.exportMultiLineHeadExcel(response);
    }

    @PostMapping("/export/v5")
    public void exportExcelV5(HttpServletResponse response) {
        pandaService.export1mPandaExcel(response);
    }

    @PostMapping("/export/v6")
    public ServerResponse<Long> exportExcelV6() {
        return ServerResponse.success(pandaService.export1mPandaExcelV2());
    }

    @PostMapping("/export/v7")
    public void exportExcelV7(HttpServletResponse response) {
        pandaService.exportPandaStatisticsData(response);
    }
}
