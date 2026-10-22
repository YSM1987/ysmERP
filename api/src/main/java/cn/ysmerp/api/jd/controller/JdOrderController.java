package cn.ysmerp.api.jd.controller;


import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.common.mq.MqUtils;
import cn.ysmerp.model.entity.JdOrder;
import cn.ysmerp.model.bo.JdOrderBo;
import cn.ysmerp.service.JdOrderService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/jd/order")
public class JdOrderController extends BaseController {
    private final JdOrderService orderService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo orderList(JdOrderBo bo, PageQuery pageQuery) {
        PageResult<JdOrder> result = orderService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

}
