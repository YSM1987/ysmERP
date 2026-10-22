package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.bo.RefundProcessingBo;
import cn.ysmerp.model.entity.PddRefund;
import cn.ysmerp.model.bo.PddRefundBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【pdd_refund(拼多多订单退款表)】的数据库操作Service
* @createDate 2024-06-06 09:48:28
*/
public interface PddRefundService extends IService<PddRefund> {
    PageResult<PddRefund> queryPageList(PddRefundBo bo, PageQuery pageQuery);
    ResultVo<Integer>  saveRefund(Long shopId, PddRefund refund);
    ResultVo<Long> refundHandle(RefundProcessingBo bo, String userName);
}
