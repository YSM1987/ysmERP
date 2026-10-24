package cn.ysmerp.api.controller;


import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.model.entity.OGoodsBrand;
import cn.ysmerp.service.OGoodsBrandService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

@AllArgsConstructor
@RestController
@RequestMapping("/api/erp-api/goods_brand")
public class GoodsBrandController extends BaseController {
    private final OGoodsBrandService brandService;
    @GetMapping("/list")
    public TableDataInfo skuList(OGoodsBrand bo, PageQuery pageQuery)
    {
        var pageList = brandService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 获取商品品牌详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(brandService.getById(id));
    }

    /**
     * 新增商品品牌
     */
    @PostMapping
    public AjaxResult add(@RequestBody OGoodsBrand erpGoodsBrand)
    {
        if (erpGoodsBrand.getName() == null || erpGoodsBrand.getName().trim().isEmpty())
        {
            return error("品牌名不能为空");
        }
        if (erpGoodsBrand.getNum() == null || erpGoodsBrand.getNum().trim().isEmpty())
        {
            return error("品牌编码不能为空");
        }
        erpGoodsBrand.setStatus(1);
        erpGoodsBrand.setCreateBy(getUsername());
        erpGoodsBrand.setCreateTime(new Date());
        return toAjax(brandService.save(erpGoodsBrand));
    }

    /**
     * 修改商品品牌
     */
    @PutMapping
    public AjaxResult edit(@RequestBody OGoodsBrand erpGoodsBrand)
    {
        erpGoodsBrand.setUpdateBy(getUsername());
        erpGoodsBrand.setUpdateTime(new Date());
        return toAjax(brandService.updateById(erpGoodsBrand));
    }

    /**
     * 删除商品品牌
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(brandService.removeByIds(Arrays.stream(ids).toList()));
    }
}
