package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.ErpBillShopOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_shop_bill(店铺账单)】的数据库操作Service
* @createDate 2025-05-26 11:05:47
*/
public interface ErpBillShopOrderService extends IService<ErpBillShopOrder> {
    PageResult<ErpBillShopOrder> queryPageList(ErpBillShopOrder bo, PageQuery pageQuery);
}
