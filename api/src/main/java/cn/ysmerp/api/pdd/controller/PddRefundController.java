package cn.ysmerp.api.pdd.controller;


import cn.ysmerp.common.*;
import cn.ysmerp.common.mq.MqUtils;
import cn.ysmerp.model.bo.RefundProcessingBo;
import cn.ysmerp.model.entity.PddRefund;
import cn.ysmerp.model.bo.PddRefundBo;
import cn.ysmerp.service.PddRefundService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/pdd/refund")
public class PddRefundController extends BaseController {
    private final PddRefundService refundService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(PddRefundBo bo, PageQuery pageQuery) {
        PageResult<PddRefund> result = refundService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }
    /**
     *
     * @param bo
     * @return
     */
    @PostMapping("/handle")
    public AjaxResult handle(@RequestBody RefundProcessingBo bo ) {
        if (bo.getRefundId() == null) return AjaxResult.error(500, "缺少参数refundId");
        ResultVo<Long> resultVo = refundService.refundHandle(bo,getUsername());
        if(resultVo.getCode() == 0) return success();
        else return AjaxResult.error(resultVo.getMsg());
    }
}
