package cn.ysmerp.api.pdd.controller;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.ResultVoEnum;
import cn.ysmerp.enums.EnumShopType;
import cn.ysmerp.model.entity.OLogisticsCompany;
import cn.ysmerp.model.entity.OShopPlatform;
import cn.ysmerp.service.OLogisticsCompanyService;
import cn.ysmerp.service.OShopPlatformService;
import cn.ysmerp.open.common.ApiResultVo;
import cn.ysmerp.open.pdd.PddLogisticsApiHelper;
import cn.ysmerp.open.pdd.model.LogisticsCompany;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/pdd/logistics")
@RestController
@AllArgsConstructor
public class PddLogisticsApiController {
    private final OShopPlatformService platformService;
    private final OLogisticsCompanyService companyService;
    /**
     * 拉取快递公司
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_logistics", method = RequestMethod.POST)
    public AjaxResult pullSkuList() throws Exception {
        OShopPlatform platform = platformService.getById(EnumShopType.PDD.getIndex());

        if (!StringUtils.hasText(platform.getAppKey())) {
            return AjaxResult.error( "平台配置错误，没有找到AppKey");
        }
        if (!StringUtils.hasText(platform.getAppSecret())) {
            return AjaxResult.error("平台配置错误，没有找到AppSercet");
        }
        ApiResultVo<LogisticsCompany> resultVo = PddLogisticsApiHelper.getLogisticsCompany(platform.getAppKey(), platform.getAppSecret());
        if (resultVo.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
            for (var item:resultVo.getList()){
                OLogisticsCompany oLogisticsCompany = new OLogisticsCompany();
                oLogisticsCompany.setPlatformId(EnumShopType.PDD.getIndex());
                oLogisticsCompany.setShopId(0L);
                oLogisticsCompany.setLogisticsId(item.getId().toString());
                oLogisticsCompany.setCode(item.getCode());
                oLogisticsCompany.setName(item.getLogisticsCompany());
                oLogisticsCompany.setStatus(0);
                companyService.save(oLogisticsCompany);
                log.info("=========添加物流快递公司===========");
            }
            log.info("=========PDD物流快递公司添加完成========");
        }else{
            return AjaxResult.error(resultVo.getMsg());
        }
        return AjaxResult.success("开源版本暂未实现");
    }
}
