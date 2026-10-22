package cn.ysmerp.api.controller;

import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.model.entity.OShopPullLogs;
import cn.ysmerp.service.OShopPullLogsService;
import cn.ysmerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/shop")
public class ShopPullLogsController extends BaseController {
    private final OShopPullLogsService pullLogsService;

    @GetMapping("/pull_logs_list")
    public TableDataInfo list(OShopPullLogs logs, PageQuery pageQuery)
    {
        var pageList = pullLogsService.queryPageList(logs,pageQuery);
        return getDataTable(pageList);
    }
}
