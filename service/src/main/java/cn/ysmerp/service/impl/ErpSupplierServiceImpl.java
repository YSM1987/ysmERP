package cn.ysmerp.service.impl;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.mapper.ErpMerchantMapper;
import cn.ysmerp.mapper.ErpSupplierMapper;
import cn.ysmerp.model.entity.ErpSupplier;
import cn.ysmerp.service.ErpSupplierService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_goods_supplier】的数据库操作Service实现
* @createDate 2024-09-07 16:35:43
*/
@Slf4j
@AllArgsConstructor
@Service
public class ErpSupplierServiceImpl extends ServiceImpl<ErpSupplierMapper, ErpSupplier>
    implements ErpSupplierService {
    private final ErpSupplierMapper mapper;

    @Override
    public PageResult<ErpSupplier> queryPageList(ErpSupplier bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpSupplier> queryWrapper = new LambdaQueryWrapper<ErpSupplier>();
        queryWrapper.eq(bo.getMerchantId()!=null, ErpSupplier::getMerchantId,bo.getMerchantId());
        queryWrapper.eq(bo.getShopId()!=null, ErpSupplier::getShopId,bo.getShopId());
        queryWrapper.eq(bo.getIsShipper()!=null, ErpSupplier::getIsShipper,bo.getIsShipper());
        queryWrapper.like(StringUtils.hasText(bo.getName()), ErpSupplier::getName, bo.getName());
        queryWrapper.like(StringUtils.hasText(bo.getNumber()), ErpSupplier::getNumber, bo.getNumber());
        queryWrapper.like(StringUtils.hasText(bo.getLinkMan()), ErpSupplier::getLinkMan, bo.getLinkMan());
        // 默认按主键倒序，保证新增数据出现在第一页
        queryWrapper.orderByDesc(ErpSupplier::getId);

        Page<ErpSupplier> pages = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




