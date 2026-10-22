package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.model.entity.OmsWeiGoodsSku;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author TW
* @description 针对表【oms_wei_goods_sku】的数据库操作Service
* @createDate 2024-06-03 16:51:29
*/
public interface OmsWeiGoodsSkuService extends IService<OmsWeiGoodsSku> {
    PageResult<OmsWeiGoodsSku> queryPageList(OmsWeiGoodsSku bo, PageQuery pageQuery);

    ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo);
}
