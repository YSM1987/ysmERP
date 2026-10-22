package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.JdRefund;
import cn.ysmerp.model.bo.JdAfterBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_jd_after_sale(京东退款与售后表)】的数据库操作Service
* @createDate 2024-09-17 17:20:26
*/
public interface JdRefundService extends IService<JdRefund> {
    PageResult<JdRefund> queryPageList(JdAfterBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveRefund(Long shopId, JdRefund after);
}
