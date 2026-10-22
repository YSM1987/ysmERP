package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.model.entity.JdGoodsSku;
import cn.ysmerp.model.bo.JdGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【jd_goods_sku】的数据库操作Service
* @createDate 2024-03-09 20:44:11
*/
public interface JdGoodsSkuService extends IService<JdGoodsSku> {
    PageResult<JdGoodsSku> queryPageList(JdGoodsBo bo, PageQuery pageQuery);
    ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo);
}
