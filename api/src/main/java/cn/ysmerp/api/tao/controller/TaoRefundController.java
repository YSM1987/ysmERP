package cn.ysmerp.api.tao.controller;


import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.common.mq.MqUtils;
import cn.ysmerp.model.entity.TaoRefund;
import cn.ysmerp.model.bo.TaoRefundBo;
import cn.ysmerp.service.TaoRefundService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/tao/refund")
public class TaoRefundController extends BaseController {
    private final TaoRefundService refundService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(TaoRefundBo bo, PageQuery pageQuery) {
        PageResult<TaoRefund> result = refundService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

}
