package cn.ysmerp.api.controller;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.model.bo.RefundProcessingBo;
import cn.ysmerp.model.bo.RefundSearchBo;
import cn.ysmerp.security.common.BaseController;
import cn.ysmerp.service.ORefundService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/erp-api/refund")
public class RefundController extends BaseController {

    private final ORefundService refundService;

    @GetMapping("/list")
    public TableDataInfo list(RefundSearchBo bo, PageQuery pageQuery) {
        var pageList = refundService.queryPageList(bo, pageQuery);
        return getDataTable(pageList);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(refundService.selectById(id));
    }

    @PostMapping("/pushErp/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return success();
    }

    @PostMapping("/processing")
    public AjaxResult refundProcessing(@RequestBody RefundProcessingBo bo) {
        if (bo.getRefundId() == null) return AjaxResult.error(500, "缺少参数refundId");
        ResultVo<Long> resultVo = refundService.refundProcessing(bo, getUsername());
        if (resultVo.getCode() == 0) return success();
        else return AjaxResult.error(resultVo.getMsg());
    }
}
