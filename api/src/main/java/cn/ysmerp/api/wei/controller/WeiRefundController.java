package cn.ysmerp.api.wei.controller;



import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.common.mq.MqUtils;
import cn.ysmerp.model.bo.WeiRefundBo;
import cn.ysmerp.model.entity.OmsWeiRefund;
import cn.ysmerp.service.OmsWeiRefundService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/wei/refund")
public class WeiRefundController extends BaseController {
    private final OmsWeiRefundService refundService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(WeiRefundBo bo, PageQuery pageQuery) {
        PageResult<OmsWeiRefund> result = refundService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }



}
