package cn.ysmerp.api.jd.controller;

import cn.ysmerp.common.*;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.model.entity.JdGoods;
import cn.ysmerp.model.entity.JdGoodsSku;
import cn.ysmerp.model.bo.JdGoodsBo;
import cn.ysmerp.service.JdGoodsService;
import cn.ysmerp.service.JdGoodsSkuService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/jd/goods")
@RestController
@AllArgsConstructor
public class JdGoodsController extends BaseController {
    private final JdGoodsService goodsService;
    private final JdGoodsSkuService skuService;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(JdGoodsBo bo, PageQuery pageQuery) {
        PageResult<JdGoods> result = goodsService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @RequestMapping(value = "/skuList", method = RequestMethod.GET)
    public TableDataInfo skuList(JdGoodsBo bo, PageQuery pageQuery) {
        PageResult<JdGoodsSku> result = skuService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    /**
     * 获取店铺订单详细信息
     */
    @GetMapping(value = "/sku/{id}")
    public AjaxResult getSkuInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(skuService.getById(id));
    }
    @PostMapping(value = "/sku/linkErp")
    public AjaxResult linkErp(@RequestBody LinkErpGoodsSkuBo bo)
    {
        if(StringUtils.isBlank(bo.getId())){
            return AjaxResult.error(500,"缺少参数Id");
        }
        if(StringUtils.isBlank(bo.getErpGoodsSkuId())){
            return AjaxResult.error(500,"缺少参数oGoodsSkuId");
        }

        ResultVo resultVo = skuService.linkErpGoodsSku(bo);
        if(resultVo.getCode()==0)
            return success();
        else return AjaxResult.error(resultVo.getMsg());
    }

}
