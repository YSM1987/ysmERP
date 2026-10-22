package cn.ysmerp.mapper;

import cn.ysmerp.model.bo.OrderItemListBo;
import cn.ysmerp.model.entity.OOrderItem;
import cn.ysmerp.model.vo.OrderItemListVo;
import cn.ysmerp.model.vo.SalesTopSkuVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_order_item(OMS订单明细表)】的数据库操作Mapper
* @createDate 2025-06-01 14:08:04
* @Entity cn.ysmerp.model.entity.OOrderItem
*/
public interface OOrderItemMapper extends BaseMapper<OOrderItem> {
    Page<OrderItemListVo> selectPageVo(@Param("page") Page<OrderItemListVo> page, @Param("qw") OrderItemListBo qw);
    List<OrderItemListVo> selectOrderItemListByOrderId(@Param("orderId") Long orderId);
    List<SalesTopSkuVo> selectTopSku(@Param("startDate") String startDate, @Param("endDate") String endDate);
}




