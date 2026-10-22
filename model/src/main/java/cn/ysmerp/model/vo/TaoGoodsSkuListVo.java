package cn.ysmerp.model.vo;

import cn.ysmerp.model.entity.TaoGoodsSku;
import lombok.Data;

@Data
public class TaoGoodsSkuListVo extends TaoGoodsSku {
    private String title;
    private String picUrl;
    private String outerErpSkuId;
    private Long shopId;
}
