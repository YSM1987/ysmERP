package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.bo.MerchantAddBo;
import cn.ysmerp.model.entity.ErpMerchant;
import cn.ysmerp.model.query.MerchantQuery;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_tenant(租户用户表)】的数据库操作Service
* @createDate 2024-06-23 11:10:08
*/
public interface ErpMerchantService extends IService<ErpMerchant> {
    PageResult<ErpMerchant> queryPageList(MerchantQuery bo, PageQuery pageQuery);
    ErpMerchant selectUserByUserName(String userName);
    void updateByUserId(ErpMerchant tenant, Long userId);
    ResultVo<ErpMerchant> add(MerchantAddBo bo,String createBy);

}
