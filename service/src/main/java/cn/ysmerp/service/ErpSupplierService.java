package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.ErpSupplier;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_goods_supplier】的数据库操作Service
* @createDate 2024-09-07 16:35:43
*/
public interface ErpSupplierService extends IService<ErpSupplier> {
    PageResult<ErpSupplier> queryPageList(ErpSupplier bo, PageQuery pageQuery);

}
