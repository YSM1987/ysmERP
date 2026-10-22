package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.model.entity.TaoGoodsSku;
import cn.ysmerp.model.bo.TaoGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【tao_goods_sku】的数据库操作Service
* @createDate 2024-02-29 19:01:35
*/
public interface TaoGoodsSkuService extends IService<TaoGoodsSku> {
    PageResult<TaoGoodsSku> queryPageList(TaoGoodsBo bo, PageQuery pageQuery);
    ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo);
}
