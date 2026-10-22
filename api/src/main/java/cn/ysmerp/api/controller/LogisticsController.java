package cn.ysmerp.api.controller;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.model.entity.OLogisticsCompany;
import cn.ysmerp.service.OLogisticsCompanyService;
import cn.ysmerp.service.OShopPlatformService;
import cn.ysmerp.service.OShopService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺Controller
 * 
 * @author ysmERP
 * @date 2023-12-31
 */
@AllArgsConstructor
@RestController
@RequestMapping("/shop")
public class LogisticsController extends BaseController {
    private final OLogisticsCompanyService logisticsCompanyService;
    private final OShopService shopService;
    private final OShopPlatformService platformService;



    @GetMapping("/logistics_status")
    public TableDataInfo logisticsStatusList(Integer status, Integer shopType, Integer shopId)
    {
        if(status==null) status=1;
        if(shopType==null)shopType=0;
        return getDataTable(logisticsCompanyService.queryListByStatus(status,shopType, shopId));
    }
    /**
     * 查询店铺列表logistics
     */
    @GetMapping("/logistics")
    public TableDataInfo logisticsList(Integer type ,String name,String code, PageQuery pageQuery)
    {
        if(type==null)type=0;
        PageResult<OLogisticsCompany> result = logisticsCompanyService.queryPageList(type, name,code, pageQuery);
        return getDataTable(result);
    }

    @PostMapping("/logistics/add")
    public AjaxResult add(@RequestBody OLogisticsCompany company)
    {
//        company.setPlatformId(0);
        return toAjax(logisticsCompanyService.save(company));
    }

    @PutMapping("/logistics/updateStatus")
    public AjaxResult logisticsUpdateStatus(@RequestBody OLogisticsCompany company)
    {
        Integer newStatus = null;
        if(company.getStatus()==null || company.getStatus().intValue() ==0){
            newStatus = 1;
        }else{
            newStatus = 0;
        }
        return toAjax(logisticsCompanyService.updateStatus(company.getId(),newStatus));
    }

    @PutMapping("/logistics/update")
    public AjaxResult edit(@RequestBody OLogisticsCompany shop)
    {
        return toAjax(logisticsCompanyService.updateById(shop));
    }

    @GetMapping(value = "/logistics/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(logisticsCompanyService.getById(id));
    }
    @DeleteMapping("/logistics/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(logisticsCompanyService.removeById(id));
    }
}
