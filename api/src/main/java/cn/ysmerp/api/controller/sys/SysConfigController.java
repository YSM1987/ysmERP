package cn.ysmerp.api.controller.sys;


import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.model.entity.SysConfig;
import cn.ysmerp.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sys-api/system/config")
public class SysConfigController {
    private final SysConfigService configService;

    @GetMapping(value = "/getConfigValue/{key}")
    public AjaxResult getConfigValue(@PathVariable String key)
    {
        List<SysConfig> list = configService.list(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if(list!=null && !list.isEmpty())
        {
            return AjaxResult.success(list.get(0));
        }else{
            return AjaxResult.success();
        }
    }
}
