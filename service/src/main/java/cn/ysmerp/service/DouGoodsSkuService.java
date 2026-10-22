package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.model.entity.DouGoodsSku;
import cn.ysmerp.model.bo.DouGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【dou_goods_sku(抖店商品Sku表)】的数据库操作Service
* @createDate 2024-05-31 17:23:21
*/
public interface DouGoodsSkuService extends IService<DouGoodsSku> {
    PageResult<DouGoodsSku> queryPageList(DouGoodsBo bo, PageQuery pageQuery);
    ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo);
}
