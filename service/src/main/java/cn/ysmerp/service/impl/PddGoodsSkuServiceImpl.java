package cn.ysmerp.service.impl;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.mapper.goods.OGoodsMapper;
import cn.ysmerp.mapper.goods.OGoodsSkuMapper;
import cn.ysmerp.model.bo.PddGoodsSkuBo;
import cn.ysmerp.model.entity.OGoods;
import cn.ysmerp.model.entity.OGoodsSku;
import cn.ysmerp.model.entity.PddGoods;
import cn.ysmerp.model.entity.PddGoodsSku;
import cn.ysmerp.mapper.PddGoodsMapper;
import cn.ysmerp.mapper.PddGoodsSkuMapper;
import cn.ysmerp.service.PddGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author TW
* @description 针对表【pdd_goods_sku(pdd商品SKU表)】的数据库操作Service实现
* @createDate 2024-06-04 17:11:49
*/
@AllArgsConstructor
@Service
public class PddGoodsSkuServiceImpl extends ServiceImpl<PddGoodsSkuMapper, PddGoodsSku>
    implements PddGoodsSkuService {
    private final PddGoodsSkuMapper mapper;
    private final PddGoodsMapper pddGoodsMapper;
    private final OGoodsSkuMapper oGoodsSkuMapper;
    private final OGoodsMapper oGoodsMapper;
    @Override
    public PageResult<PddGoodsSku> queryPageList(PddGoodsSkuBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PddGoodsSku> ew = new LambdaQueryWrapper<PddGoodsSku>()
                .eq(bo.getShopId()!=null,PddGoodsSku::getShopId,bo.getShopId())
                .eq(bo.getGoodsId()!=null,PddGoodsSku::getGoodsId,bo.getGoodsId())
                .eq(StringUtils.hasText(bo.getSkuId()),PddGoodsSku::getSkuId,bo.getSkuId())
                .eq(bo.getErpSkuId()!=null,PddGoodsSku::getErpGoodsSkuId,bo.getErpSkuId())
                .eq(StringUtils.hasText(bo.getOuterId()),PddGoodsSku::getOuterId,bo.getOuterId())
                .eq(bo.getIsSkuOnsale()!=null,PddGoodsSku::getIsSkuOnsale,bo.getIsSkuOnsale())
                ;
        IPage<PddGoodsSku> result = mapper.selectPage(pageQuery.build(), ew);
        return PageResult.build(result);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo) {
        OGoodsSku oGoodsSku = oGoodsSkuMapper.selectById(bo.getErpGoodsSkuId());
        if(oGoodsSku == null) return ResultVo.error("未找到系统商品sku");

        OGoods oGoods=oGoodsMapper.selectById(oGoodsSku.getGoodsId());
        if(oGoods == null){
            return ResultVo.error("未找到系统商品");
        }

        PddGoodsSku taoGoodsSku = mapper.selectById(bo.getId());
        if(taoGoodsSku == null) {
            return ResultVo.error("PDD商品sku数据不存在");
        }
        List<PddGoods> jdGoods = pddGoodsMapper.selectList(new LambdaQueryWrapper<PddGoods>().eq(PddGoods::getGoodsId, taoGoodsSku.getGoodsId()));
        if(jdGoods==null||jdGoods.size()==0){
            return ResultVo.error("PDD商品数据不存在");
        }

        PddGoodsSku sku = new PddGoodsSku();
        sku.setId(Long.parseLong(bo.getId()));
        sku.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        sku.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
        mapper.updateById(sku);

        PddGoods goodsUp=new PddGoods();
        goodsUp.setId(jdGoods.get(0).getId());
        goodsUp.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        pddGoodsMapper.updateById(goodsUp);
        return ResultVo.success();
    }
}




