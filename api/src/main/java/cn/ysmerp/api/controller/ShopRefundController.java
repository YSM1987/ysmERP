package cn.ysmerp.api.controller;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.ResultVo;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.model.entity.ShopRefund;
import cn.ysmerp.model.request.RefundPullRequest;
import cn.ysmerp.security.common.BaseController;
import cn.ysmerp.service.ShopRefundService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/shop/refund")
public class ShopRefundController extends BaseController {

    private final ShopRefundService shopRefundService;

    @GetMapping("/list")
    public TableDataInfo list(ShopRefund bo, PageQuery pageQuery) {
        PageResult<ShopRefund> pageList = shopRefundService.queryPageList(bo, pageQuery);
        return getDataTable(pageList);
    }

    @PostMapping("/pull_list")
    public AjaxResult pullList(@RequestBody RefundPullRequest req) {

        return AjaxResult.error("");
    }

    @PostMapping("/pull_detail")
    public AjaxResult pullDetail(@RequestBody RefundPullRequest req) {
        return AjaxResult.error("");
    }

    @PostMapping("/push_oms")
    public AjaxResult pushOms(@RequestBody Map<String, Object> body) {
        return AjaxResult.error("");
    }
}
