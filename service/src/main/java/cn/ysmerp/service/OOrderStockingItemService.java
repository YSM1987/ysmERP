package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.OOrderStockingItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 针对表【o_supplier_ship_order_item】的数据库操作Service
 */
public interface OOrderStockingItemService extends IService<OOrderStockingItem> {

    /**
     * 根据shipOrderId查询明细列表
     */
    List<OOrderStockingItem> listByShipOrderId(Long shipOrderId);

}
