package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;

import cn.ysmerp.model.entity.OShipment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_shipment(发货记录表)】的数据库操作Service
* @createDate 2025-06-01 23:22:40
*/
public interface ErpShipmentService extends IService<OShipment> {
    PageResult<OShipment> queryPageList(OShipment shipping, PageQuery pageQuery);
    OShipment queryDetailById(Long id);
}
