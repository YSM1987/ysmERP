package cn.ysmerp.service;

import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.ErpWarehouse;
import cn.ysmerp.model.request.WarehouseCloudQuery;
import cn.ysmerp.model.request.WarehousePageQuery;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【erp_cloud_warehouse】的数据库操作Service
* @createDate 2025-07-07 15:10:27
*/
public interface ErpWarehouseService extends IService<ErpWarehouse> {
    PageResult<ErpWarehouse> queryPageList(WarehousePageQuery query );

    List<ErpWarehouse> getCloudWarehouseList(WarehouseCloudQuery query);
    List<ErpWarehouse> getJdWarehouseList(ErpWarehouse bo);
    List<ErpWarehouse> getJkyWarehouseList(ErpWarehouse bo);
    List<ErpWarehouse> getWarehouseList(ErpWarehouse bo);
    ResultVo<Long> saveErpCloudWarehouse(List<ErpWarehouse> list);



}
