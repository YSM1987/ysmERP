package cn.ysmerp.service.impl;

import cn.ysmerp.common.ResultVo;
import cn.ysmerp.domain.bo.LinkErpGoodsSkuBo;
import cn.ysmerp.mapper.goods.OGoodsMapper;
import cn.ysmerp.mapper.goods.OGoodsSkuMapper;
import cn.ysmerp.model.entity.OGoods;
import cn.ysmerp.model.entity.OGoodsSku;
import cn.ysmerp.model.entity.JdGoods;
import cn.ysmerp.mapper.JdGoodsMapper;
import cn.ysmerp.service.JdGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.model.entity.JdGoodsSku;
import cn.ysmerp.model.bo.JdGoodsBo;
import cn.ysmerp.mapper.JdGoodsSkuMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
* @author qilip
* @description 针对表【jd_goods_sku】的数据库操作Service实现
* @createDate 2024-03-09 20:44:11
*/
@AllArgsConstructor
@Service
public class JdGoodsSkuServiceImpl extends ServiceImpl<JdGoodsSkuMapper, JdGoodsSku>
    implements JdGoodsSkuService {
    private final JdGoodsSkuMapper mapper;
    private final JdGoodsMapper jdGoodsMapper;
    private final OGoodsSkuMapper oGoodsSkuMapper;
    private final OGoodsMapper oGoodsMapper;

    @Override
    public PageResult<JdGoodsSku> queryPageList(JdGoodsBo bo, PageQuery pageQuery) {
        if(StringUtils.hasText(bo.getOuterId())){
            bo.setOuterId(bo.getOuterId().trim());
        }
//        IPage<JdGoodsSkuListVo> result = mapper.selectSkuPageList(pageQuery.build(), bo.getShopId(),bo.getWareId(),bo.getSkuId(),bo.getOuterId(),bo.getHasLink());
//        return PageResult.build(result);

        LambdaQueryWrapper<JdGoodsSku> queryWrapper = new LambdaQueryWrapper<JdGoodsSku>()
                .eq(bo.getShopId()!=null,JdGoodsSku::getShopId,bo.getShopId())

                ;

        Page<JdGoodsSku> goodsPage = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(goodsPage);

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

        JdGoodsSku taoGoodsSku = mapper.selectById(bo.getId());
        if(taoGoodsSku == null) {
            return ResultVo.error("JD商品sku数据不存在");
        }
        List<JdGoods> jdGoods = jdGoodsMapper.selectList(new LambdaQueryWrapper<JdGoods>().eq(JdGoods::getWareId, taoGoodsSku.getWareId()));
        if(jdGoods==null||jdGoods.size()==0){
            return ResultVo.error("JD商品数据不存在");
        }

        JdGoodsSku sku = new JdGoodsSku();
        sku.setId(Long.parseLong(bo.getId()));
        sku.setLogo(oGoodsSku.getColorImage());
        sku.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        sku.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
        mapper.updateById(sku);

        JdGoods goodsUp=new JdGoods();
        goodsUp.setId(jdGoods.get(0).getId());
        goodsUp.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        goodsUp.setLogo(oGoods.getImage());
        jdGoodsMapper.updateById(goodsUp);
        return ResultVo.success();
    }

}




