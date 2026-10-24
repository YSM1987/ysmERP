package cn.ysmerp.api.controller.sup;

import cn.ysmerp.common.*;
import cn.ysmerp.model.entity.ErpSupplier;
import cn.ysmerp.security.common.BaseController;
import cn.ysmerp.service.ErpSupplierService;
import cn.ysmerp.service.OShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@AllArgsConstructor
@RestController
@RequestMapping("/api/erp-api/supplier")
public class SupplierController extends BaseController {
    private final ErpSupplierService supplierService;

    private final OShopService shopService;

    /**
     * 所有供应商list
     *
     * @param bo
     * @param pageQuery
     * @return
     */
    @GetMapping("/list_all")
    public TableDataInfo list_all(ErpSupplier bo, PageQuery pageQuery) {

        var pageList = supplierService.list(new LambdaQueryWrapper<ErpSupplier>()
                .eq(ErpSupplier::getIsDelete, 0)
                .eq(bo.getIsShipper() != null, ErpSupplier::getIsShipper, bo.getIsShipper())
                .eq(bo.getMerchantId() != null, ErpSupplier::getMerchantId, bo.getMerchantId())
                .eq(bo.getShopId() != null, ErpSupplier::getShopId, bo.getShopId())
        );
        return getDataTable(pageList);
    }

    @GetMapping("/list")
    public TableDataInfo list(ErpSupplier bo, PageQuery pageQuery) {
        var pageList = supplierService.queryPageList(bo, pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(supplierService.getById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PostMapping
    public AjaxResult add(@RequestBody ErpSupplier scmSupplier) {
        if (scmSupplier.getName() == null || scmSupplier.getName().trim().isEmpty())
            return AjaxResult.error("供应商名称不能为空");
        if (scmSupplier.getNumber() == null || scmSupplier.getNumber().trim().isEmpty())
            return AjaxResult.error("供应商编码不能为空");
        // 未指定商户时按总部自营处理，避免因缺少 merchantId 直接新增失败
        if (scmSupplier.getMerchantId() == null)
            scmSupplier.setMerchantId(0L);
        scmSupplier.setCreateBy(getUsername());
        scmSupplier.setCreateTime(new Date());
        scmSupplier.setIsDelete(0);
        boolean saved = supplierService.save(scmSupplier);
        return saved ? AjaxResult.success("新增成功") : AjaxResult.error("新增失败，请重试");
    }

    /**
     * 修改【请填写功能名称】
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ErpSupplier scmSupplier) {
        if (scmSupplier.getId() == null) return AjaxResult.error("缺少参数：id");
        ErpSupplier byId = supplierService.getById(scmSupplier.getId());
        if (byId == null) return AjaxResult.error("供应商数据不存在");
        scmSupplier.setUpdateTime(new Date());
        return toAjax(supplierService.updateById(scmSupplier));
    }

    /**
     * 删除【请填写功能名称】
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) return AjaxResult.error("缺少参数id");

        for (Long id : ids) {
            ErpSupplier byId = supplierService.getById(id);
            if (byId == null) return AjaxResult.error("供应商数据不存在");
            supplierService.removeById(id);
        }
        return AjaxResult.success();
    }


}
