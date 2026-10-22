package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;

import cn.ysmerp.model.entity.OAfterSale;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_after_sale(OMS售后处理表)】的数据库操作Service
* @createDate 2024-09-15 21:30:30
*/
public interface OAfterSaleService extends IService<OAfterSale> {
    PageResult<OAfterSale> queryPageList(OAfterSale bo, PageQuery pageQuery);
}
