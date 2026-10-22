package cn.ysmerp.api.controller.sys;


import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.PageQuery;
import cn.ysmerp.common.PageResult;
import cn.ysmerp.common.TableDataInfo;
import cn.ysmerp.model.entity.SysTask;
import cn.ysmerp.service.SysTaskService;
import cn.ysmerp.service.SysTaskLogsService;
import cn.ysmerp.security.common.BaseController;

import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 
 * @author ysmERP
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/sys-api/system/task")
public class SysTaskController extends BaseController
{
    private final SysTaskService taskService;
    private final SysTaskLogsService taskLogsService;
    @GetMapping("/list")
    public TableDataInfo list(PageQuery pageQuery)
    {
        PageResult<SysTask> pageResult = taskService.queryPageList(pageQuery);
        return getDataTable(pageResult);
    }

    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId)
    {
        return success(taskService.getById(taskId));
    }

    @GetMapping(value = "/logs/{taskId}")
    public AjaxResult getLogs(@PathVariable Integer taskId)
    {
        return success(taskLogsService.queryPageList(taskId,new PageQuery()));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody SysTask task)
    {
        if(StringUtils.hasText(task.getCron())&&!task.getCron().equals("-")){
            task.setStatus(1);
        }else{
            task.setStatus(0);
        }
        return toAjax(taskService.updateById(task));
    }
}
