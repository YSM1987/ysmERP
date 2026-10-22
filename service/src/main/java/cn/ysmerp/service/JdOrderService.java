package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.JdOrder;
import cn.ysmerp.model.bo.JdOrderBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【jd_order(京东订单表)】的数据库操作Service
* @createDate 2024-03-09 11:29:59
*/
public interface JdOrderService extends IService<JdOrder> {
    PageResult<JdOrder> queryPageList(JdOrderBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveOrder(Long shopId, JdOrder order);
    JdOrder  queryDetailByOrderId(Long orderId);
}
