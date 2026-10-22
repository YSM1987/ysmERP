package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.ShopRefund;
import cn.ysmerp.model.request.SaleOrderAfterAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ShopRefundService extends IService<ShopRefund> {
    PageResult<ShopRefund> queryPageList(ShopRefund bo, PageQuery pageQuery);
    ResultVo<Long> saveRefund(Long shopId, ShopRefund bo);
    ResultVo<Long> addRefund(SaleOrderAfterAddRequest addRequest);
}
