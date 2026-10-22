package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.ErpBillShopAccounts;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_bill_shop_accounts(店铺账目表)】的数据库操作Service
* @createDate 2025-05-28 15:02:57
*/
public interface ErpBillShopAccountsService extends IService<ErpBillShopAccounts> {
    PageResult<ErpBillShopAccounts> queryPageList(ErpBillShopAccounts bo, PageQuery pageQuery);
}
