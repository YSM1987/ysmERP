package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.OGoodsBrand;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_goods_brand】的数据库操作Service
* @createDate 2024-09-07 16:11:56
*/
public interface OGoodsBrandService extends IService<OGoodsBrand> {
    PageResult<OGoodsBrand> queryPageList(OGoodsBrand bo, PageQuery pageQuery);
}
