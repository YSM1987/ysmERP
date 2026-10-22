package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.ShopOrderItem;
import cn.ysmerp.model.query.ShopOrderQueryBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shop_order_item】的数据库操作Service
* @createDate 2025-07-15 11:36:42
*/
public interface ShopOrderItemService extends IService<ShopOrderItem> {
    PageResult<ShopOrderItem> queryPageList(ShopOrderQueryBo bo, PageQuery pageQuery);
}
