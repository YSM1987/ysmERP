package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.PddGoods;
import cn.ysmerp.model.bo.PddGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【pdd_goods(pdd商品表)】的数据库操作Service
* @createDate 2024-06-04 17:11:49
*/
public interface PddGoodsService extends IService<PddGoods> {
    PageResult<PddGoods> queryPageList(PddGoodsBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveGoods(Long shopId, PddGoods goods);
}
