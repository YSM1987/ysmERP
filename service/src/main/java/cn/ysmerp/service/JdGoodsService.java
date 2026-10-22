package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;

import cn.ysmerp.model.entity.JdGoods;
import cn.ysmerp.model.entity.JdGoodsSku;
import cn.ysmerp.model.bo.JdGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author qilip
* @description 针对表【jd_goods】的数据库操作Service
* @createDate 2024-03-09 11:29:59
*/
public interface JdGoodsService extends IService<JdGoods> {
    PageResult<JdGoods> queryPageList(JdGoodsBo bo, PageQuery pageQuery);
    ResultVo<Integer> saveGoods(Long shopId, JdGoods goods);
    ResultVo<Integer> saveGoodsSku(Long shopId, JdGoodsSku goodsSku);
}
