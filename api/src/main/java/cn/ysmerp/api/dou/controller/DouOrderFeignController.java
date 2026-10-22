package cn.ysmerp.api.dou.controller;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.model.entity.DouOrder;
import cn.ysmerp.service.DouOrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/open-api/dou/order")
public class DouOrderFeignController  {
    private final DouOrderService orderService;
    @GetMapping(value = "/get_detail")
    public AjaxResult getInfo(String orderId)
    {
        DouOrder order = orderService.queryDetailByOrderId(orderId);
        if(order==null) return AjaxResult.error(404,"没有找到订单");
        else return AjaxResult.success(order);
    }
}
