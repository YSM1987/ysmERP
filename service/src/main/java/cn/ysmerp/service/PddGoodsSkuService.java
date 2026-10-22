package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.model.bo.PddGoodsSkuBo;
import cn.ysmerp.model.entity.PddGoodsSku;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【pdd_goods_sku(pdd商品SKU表)】的数据库操作Service
* @createDate 2024-06-04 17:11:49
*/
public interface PddGoodsSkuService extends IService<PddGoodsSku> {
    PageResult<PddGoodsSku> queryPageList(PddGoodsSkuBo bo, PageQuery pageQuery);
    ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo);
}
