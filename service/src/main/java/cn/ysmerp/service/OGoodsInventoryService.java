package cn.ysmerp.service;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.model.entity.OGoodsInventory;
import cn.ysmerp.model.bo.GoodsInventoryModifyBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_goods_inventory(商品库存表)】的数据库操作Service
* @createDate 2024-09-23 22:39:50
*/
public interface OGoodsInventoryService extends IService<OGoodsInventory> {
    PageResult<OGoodsInventory> queryPageList(OGoodsInventory bo, PageQuery pageQuery);
    long getAllInventoryQuantity();

    /**
     * 库存变更
     * @param bo
     * @return
     */
    ResultVo<Long> modifyInventory(GoodsInventoryModifyBo bo);
}
