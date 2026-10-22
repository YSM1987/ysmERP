package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.DouGoods;
import cn.ysmerp.model.bo.DouGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【dou_goods(抖店商品表)】的数据库操作Service
* @createDate 2024-05-31 17:23:21
*/
public interface DouGoodsService extends IService<DouGoods> {
    PageResult<DouGoods> queryPageList(DouGoodsBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveGoods(Long shopId, DouGoods goods);

}
