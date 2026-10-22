package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.bo.WeiOrderBo;
import cn.ysmerp.model.entity.OmsWeiOrder;
import cn.ysmerp.model.bo.WeiOrderConfirmBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【oms_wei_order】的数据库操作Service
* @createDate 2024-06-03 16:48:31
*/
public interface OmsWeiOrderService extends IService<OmsWeiOrder> {
    PageResult<OmsWeiOrder> queryPageList(WeiOrderBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveOrder(Long shopId, OmsWeiOrder order);
    OmsWeiOrder queryDetailById(Long id);
    OmsWeiOrder queryDetailByOrderId(String orderId);
    ResultVo<Long> confirmOrder(WeiOrderConfirmBo confirmBo);
}
