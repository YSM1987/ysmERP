package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.bo.ShipStockUpBo;
import cn.ysmerp.model.entity.OOrderShipList;

import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_order_ship_list(发货-备货表（取号发货加入备货清单、分配供应商发货加入备货清单）)】的数据库操作Service
* @createDate 2025-06-01 23:07:24
*/
public interface OOrderShipListService extends IService<OOrderShipList> {
    PageResult<OOrderShipList> querySupplierPageList(ShipStockUpBo bo, PageQuery pageQuery);
    PageResult<OOrderShipList> queryWarehousePageList(ShipStockUpBo bo, PageQuery pageQuery);
}
