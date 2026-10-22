package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.TaoGoods;
import cn.ysmerp.model.bo.TaoGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【tao_goods】的数据库操作Service
* @createDate 2024-02-29 09:28:38
*/
public interface TaoGoodsService extends IService<TaoGoods> {
    PageResult<TaoGoods> queryPageList(TaoGoodsBo bo, PageQuery pageQuery);

    int saveAndUpdateGoods(Long shopId,TaoGoods goods);
}
