package cn.ysmerp.api.controller;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.model.entity.OShipment;
import cn.ysmerp.service.ErpShipmentService;
import cn.ysmerp.service.OOrderService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/shipping")
public class ShipmentController extends BaseController {
    private final ErpShipmentService shippingService;

    private final OOrderService orderService;
    @GetMapping("/list")
    public TableDataInfo list(OShipment shipping, PageQuery pageQuery)
    {
        return getDataTable(shippingService.queryPageList(shipping,pageQuery));
    }


    /**
     * 详情
     * @param id
     * @return
     */
    @GetMapping(value = "/detail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(shippingService.queryDetailById(id));
    }



}
