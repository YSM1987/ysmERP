package cn.ysmerp.api.wei.controller;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.common.mq.MqUtils;
import cn.ysmerp.model.bo.WeiOrderBo;
import cn.ysmerp.model.entity.OmsWeiOrder;
import cn.ysmerp.model.bo.WeiOrderConfirmBo;
import cn.ysmerp.service.OmsWeiOrderService;
import cn.ysmerp.security.common.BaseController;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wei/order")
public class WeiOrderController extends BaseController {
    private final OmsWeiOrderService orderService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo orderList(WeiOrderBo bo, PageQuery pageQuery) {
        PageResult<OmsWeiOrder> result = orderService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderService.queryDetailById(id));
    }


    @PostMapping("/confirmOrder")
    public AjaxResult confirmOrder(@RequestBody WeiOrderConfirmBo bo) {
        log.info("=========确认订单======={}", JSONObject.toJSONString(bo));
        if(bo.getOrderId()==null) return AjaxResult.error("订单id不能为空");
        if(StringUtils.isEmpty(bo.getReceiver())) return AjaxResult.error("缺少参数：receiver");
        if(StringUtils.isEmpty(bo.getMobile())) return AjaxResult.error("缺少参数：mobile");
        if(StringUtils.isEmpty(bo.getProvince())) return AjaxResult.error("缺少参数：province");
        if(StringUtils.isEmpty(bo.getCity())) return AjaxResult.error("缺少参数：city");
        if(StringUtils.isEmpty(bo.getTown())) return AjaxResult.error("缺少参数：town");
        if(StringUtils.isEmpty(bo.getAddress())) return AjaxResult.error("缺少参数：address");
        var result = orderService.confirmOrder(bo);
        if(result.getCode()==0) return success();
        else return AjaxResult.error(result.getMsg());
    }
}
