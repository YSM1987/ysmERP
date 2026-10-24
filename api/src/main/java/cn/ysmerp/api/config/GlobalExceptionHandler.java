package cn.ysmerp.api.config;

import cn.ysmerp.common.AjaxResult;
import cn.ysmerp.common.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理：统一记录日志并返回友好提示，避免未捕获异常直接抛出裸 500
 *
 * @author ysmERP
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request) {
        log.warn("业务异常 uri={} msg={}", request.getRequestURI(), e.getMessage());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 请求参数缺失
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public AjaxResult handleMissingParameter(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数 uri={} param={}", request.getRequestURI(), e.getParameterName());
        return AjaxResult.error("缺少必要参数：" + e.getParameterName());
    }

    /**
     * 请求体格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public AjaxResult handleNotReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("请求体解析失败 uri={} msg={}", request.getRequestURI(), e.getMessage());
        return AjaxResult.error("请求参数格式错误，请检查后重试");
    }

    /**
     * 参数校验失败（@Validated）
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public AjaxResult handleValidException(Exception e, HttpServletRequest request) {
        String message;
        if (e instanceof MethodArgumentNotValidException ex) {
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(this::formatFieldError)
                    .collect(Collectors.joining("；"));
        } else {
            message = ((BindException) e).getBindingResult().getFieldErrors().stream()
                    .map(this::formatFieldError)
                    .collect(Collectors.joining("；"));
        }
        log.warn("参数校验失败 uri={} msg={}", request.getRequestURI(), message);
        return AjaxResult.error(message.isEmpty() ? "请求参数校验失败" : message);
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方式不支持 uri={} method={}", request.getRequestURI(), e.getMethod());
        return AjaxResult.error("不支持 " + e.getMethod() + " 请求方式");
    }

    /**
     * 唯一索引冲突
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public AjaxResult handleDuplicateKey(DuplicateKeyException e, HttpServletRequest request) {
        log.error("数据重复 uri={}", request.getRequestURI(), e);
        return AjaxResult.error("数据已存在，请勿重复提交");
    }

    /**
     * 数据库约束异常（必填字段缺失、字段超长、类型不匹配等）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public AjaxResult handleDataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("数据库约束异常 uri={} params={}", request.getRequestURI(), describeParams(request), e);
        return AjaxResult.error("数据保存失败：存在必填字段缺失或字段内容超长，请检查后重试");
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request) {
        log.error("接口异常 uri={} params={}", request.getRequestURI(), describeParams(request), e);
        return AjaxResult.error("系统繁忙，请稍后重试");
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + "：" + fieldError.getDefaultMessage();
    }

    /**
     * 记录请求参数，便于定位问题（截断避免日志过大）
     */
    private String describeParams(HttpServletRequest request) {
        String query = request.getQueryString();
        String params = request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
        String result = (query == null ? "" : query) + (params.isEmpty() ? "" : " " + params);
        result = result.trim();
        return result.length() > 300 ? result.substring(0, 300) + "..." : result;
    }
}
