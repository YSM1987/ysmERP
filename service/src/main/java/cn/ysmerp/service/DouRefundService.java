package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.DouRefund;
import cn.ysmerp.model.bo.DouRefundBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【dou_refund(抖店退款表)】的数据库操作Service
* @createDate 2024-06-06 16:48:38
*/
public interface DouRefundService extends IService<DouRefund> {
    PageResult<DouRefund> queryPageList(DouRefundBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveRefund(Long shopId, DouRefund refund);
    DouRefund queryByAftersaleId(String aftersaleId);
}
