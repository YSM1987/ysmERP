package cn.ysmerp.api.jd.controller;


import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.common.mq.MqUtils;
import cn.ysmerp.model.entity.JdRefund;
import cn.ysmerp.model.bo.JdAfterBo;
import cn.ysmerp.service.JdOrderService;
import cn.ysmerp.service.JdRefundService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/jd/after")
public class JdOrderAfterSaleController extends BaseController {
    private final JdOrderService orderService;
//    private final JdRefundService refundService;
//    private final JdOrderAfterService afterService;
    private final JdRefundService afterSaleService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(JdAfterBo bo, PageQuery pageQuery) {
        PageResult<JdRefund> result = afterSaleService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }


}
