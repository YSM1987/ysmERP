package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.TaoRefund;
import cn.ysmerp.model.bo.TaoRefundBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【tao_refund(淘宝退款订单表)】的数据库操作Service
* @createDate 2024-02-29 19:01:45
*/
public interface TaoRefundService extends IService<TaoRefund> {
    PageResult<TaoRefund> queryPageList(TaoRefundBo bo, PageQuery pageQuery);
    int saveAndUpdateRefund(Long shopId,TaoRefund refund);
}
