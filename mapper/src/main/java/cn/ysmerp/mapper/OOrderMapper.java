package cn.ysmerp.mapper;

import cn.ysmerp.model.entity.OOrder;
import cn.ysmerp.model.vo.OrderDiscountVo;
import cn.ysmerp.model.vo.SalesDailyVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_order(OMS订单表)】的数据库操作Mapper
* @createDate 2025-06-01 14:05:14
* @Entity cn.ysmerp.model.entity.OOrder
*/
public interface OOrderMapper extends BaseMapper<OOrder> {
    List<OrderDiscountVo> getTaoOrderDiscount(String tid);
    List<OrderDiscountVo> getJdOrderDiscount(String orderId);

    List<SalesDailyVo> salesDaily();
    SalesDailyVo getTodaySalesDaily();
    Integer getWaitShipOrderAllCount();
}




