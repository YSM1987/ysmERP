package cn.ysmerp.api.dou.controller;

import cn.ysmerp.api.dou.DouPullRequest;
import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.ResultVoEnum;
import cn.ysmerp.enums.EnumShopType;
import cn.ysmerp.enums.HttpStatus;
import cn.ysmerp.model.entity.OShopPlatform;
import cn.ysmerp.service.OShopPlatformService;
import cn.ysmerp.service.OShopService;
import cn.ysmerp.open.common.ApiResultVo;
import cn.ysmerp.open.dou.DouTokenApiHelper;
import cn.ysmerp.open.dou.model.Token;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/dou/oauth")
public class DouOAuthController {
    private final OShopService shopService;
    private final OShopPlatformService platformService;

    @PostMapping("/update_token")
    @ResponseBody
    public AjaxResult getToken(@RequestBody DouPullRequest req)   {
        if(req.getShopId()==null) return AjaxResult.error("缺少参数：shopId");

        var shop = shopService.getById(req.getShopId());
        if (shop == null) {
            return AjaxResult.error("没有找到店铺");
        }
        if (shop.getType() != EnumShopType.DOU.getIndex()) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "店铺不是抖店店铺");
        }
        if (StringUtils.isEmpty(shop.getSellerId())  || shop.getSellerId().equals("0")) {
            return AjaxResult.error("请设置抖店平台店铺ID（shopId）");
        }
        String appKey = shop.getAppKey();
        String appSecret = shop.getAppSecret();
        if(StringUtils.isEmpty(appKey) || StringUtils.isEmpty(appSecret)) {
            OShopPlatform platform = platformService.getById(EnumShopType.DOU.getIndex());
            appKey = platform.getAppKey();
            appSecret = platform.getAppSecret();
        }


        if (!StringUtils.hasText(appKey)) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "店铺配置错误，没有找到AppKey");
        }
        if (!StringUtils.hasText(appSecret)) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "店铺配置错误，没有找到AppSecret");
        }
        ApiResultVo<Token> token = DouTokenApiHelper.getToken(appKey, appSecret, Long.parseLong(shop.getSellerId()));
        if(token.getCode()!=0) {
            return AjaxResult.error(ResultVoEnum.API_FAIL.getIndex(), token.getMsg());
        }else{
            shopService.updateSessionKey(req.getShopId(), token.getData().getAccessToken());
        }
        return AjaxResult.success();
    }
}
