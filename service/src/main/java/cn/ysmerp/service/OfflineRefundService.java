package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;

import cn.ysmerp.model.entity.OfflineRefund;
import cn.ysmerp.request.RefundSearchRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【offline_refund(线下渠道退款表)】的数据库操作Service
* @createDate 2024-09-16 13:31:26
*/
public interface OfflineRefundService extends IService<OfflineRefund> {
    PageResult<OfflineRefund> queryPageList(RefundSearchRequest bo, PageQuery pageQuery);
}
