package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.bo.ShipRecordQueryRequest;
import cn.ysmerp.model.bo.SupplierShipOrderSearchRequest;
import cn.ysmerp.model.entity.OOrderStocking;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 针对表【o_supplier_ship_order(供应商发货订单)】的数据库操作Service
 */
public interface OOrderStockingService extends IService<OOrderStocking> {

    /**
     * 已分配给供应商发货的订单list
     */
    PageResult<OOrderStocking> querySupplierShipPageList(SupplierShipOrderSearchRequest bo, PageQuery pageQuery);

    /**
     * 统一发货记录查询（根据type区分本地仓/供应商/云仓）
     */
    PageResult<OOrderStocking> queryShipRecordPageList(ShipRecordQueryRequest request, PageQuery pageQuery);

}
