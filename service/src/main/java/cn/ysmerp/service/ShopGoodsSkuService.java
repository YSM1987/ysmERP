package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.bo.BindErpGoodsSkuBo;
import cn.ysmerp.model.bo.LinkErpGoodsSkuBo;

import cn.ysmerp.model.entity.ShopGoodsSku;
import cn.ysmerp.model.query.ShopGoodsSkuQuery;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shop_goods_sku(其他渠道店铺商品SKU)】的数据库操作Service
* @createDate 2025-07-15 08:29:21
*/
public interface ShopGoodsSkuService extends IService<ShopGoodsSku> {
    PageResult<ShopGoodsSku> queryPageList(ShopGoodsSkuQuery bo, PageQuery pageQuery);

    List<ShopGoodsSku> querySkuList(Long shopGoodsId);
    ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo);

    /**
     * 查询平台skuid是否存在商品
     * @param platformSkuId
     * @param shopId
     * @return
     */
    ShopGoodsSku selectByPlatformSkuId(String platformSkuId,Long shopId);

}
