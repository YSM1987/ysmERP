package cn.ysmerp.api.dou.controller;


import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.common.mq.MqUtils;
import cn.ysmerp.model.entity.DouRefund;
import cn.ysmerp.model.bo.DouRefundBo;
import cn.ysmerp.service.DouRefundService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/dou/refund")
public class DouRefundController extends BaseController {
    private final DouRefundService refundService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(DouRefundBo bo, PageQuery pageQuery) {
        PageResult<DouRefund> result = refundService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }


}
