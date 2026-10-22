package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.bo.WeiRefundBo;
import cn.ysmerp.model.entity.OmsWeiRefund;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【oms_wei_refund(视频号小店退款)】的数据库操作Service
* @createDate 2024-06-20 17:07:27
*/
public interface OmsWeiRefundService extends IService<OmsWeiRefund> {
    PageResult<OmsWeiRefund> queryPageList(WeiRefundBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveRefund(Long shopId, OmsWeiRefund refund);
}
