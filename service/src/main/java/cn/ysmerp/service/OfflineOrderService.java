package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.bo.OfflineOrderConfirmBo;
import cn.ysmerp.model.bo.OfflineOrderCreateBo;
import cn.ysmerp.model.bo.OfflineOrderShipBo;
import cn.ysmerp.model.entity.OfflineOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.ysmerp.request.OrderSearchRequest;


/**
* @author qilip
* @description 针对表【offline_order(线下渠道订单表)】的数据库操作Service
* @createDate 2024-07-27 23:03:38
*/
public interface OfflineOrderService extends IService<OfflineOrder> {
    PageResult<OfflineOrder> queryPageList(OrderSearchRequest bo, PageQuery pageQuery);

    OfflineOrder queryDetailById(Long id);

    /**
     * 手动添加订单
     * @param bo
     * @return
     */
    Long insertOfflineOrder(OfflineOrderCreateBo bo, String createBy);

    /**
     * 确认订单
     * @param orderConfirmBo
     * @return
     */
    ResultVo<Long> confirmOrder(OfflineOrderConfirmBo orderConfirmBo);
    int orderLogistics(OfflineOrderShipBo bo, String operator);
}
