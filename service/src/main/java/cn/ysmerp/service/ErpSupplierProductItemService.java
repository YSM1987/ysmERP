package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.ErpSupplierProductItem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 1
 * @description 针对表【erp_supplier_product_item(供应商商品表(SKU维度))】的数据库操作Service
 * @createDate 2026-05-02 09:59:53
 */
public interface ErpSupplierProductItemService extends IService<ErpSupplierProductItem> {

    PageResult<ErpSupplierProductItem> queryPageList(ErpSupplierProductItem item, PageQuery pageQuery);
}
