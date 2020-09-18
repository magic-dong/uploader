package com.lzd.upload.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lzd.upload.json.MessageResult;
import com.lzd.upload.utils.StringUtils;

/**
 * 数据处理类
 * @author lzd
 * @date 2019年4月4日
 * @version
 */
public class BaseController {
	private static final Logger logger=LoggerFactory.getLogger(BaseController.class);
	
	@InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
            	try {
            		setValue(DateUtils.parseDate(text));
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(e.getMessage());
				}
            }
        });
    }
	
	
	 /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected MessageResult toAjax(int rows)
    {
        return rows > 0 ? success() : error();
    }

    /**
     * 返回成功
     */
    public MessageResult success()
    {
        return MessageResult.success();
    }

    /**
     * 返回失败消息
     */
    public MessageResult error()
    {
        return MessageResult.error();
    }

    /**
     * 返回成功消息数据
     */
    public MessageResult success(Object data)
    {
        return MessageResult.success(data);
    }

    /**
     * 返回失败消息
     */
    public MessageResult error(String message)
    {
        return MessageResult.error(message);
    }

    /**
     * 返回错误码消息
     */
    public MessageResult error(int code, String message)
    {
        return MessageResult.error(code, message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url)
    {
        return StringUtils.format("redirect:{}", url);
    }
    
    /**
     * 接口下载文件通用
     * @author lzd
     * @date 2019年8月15日:下午5:35:40
     * @param exportExcel
     * @param redirect
     * @return
     * @description
     */
    public Object download(MessageResult exportExcel,RedirectAttributes redirect){
    	boolean suceess=Boolean.valueOf(exportExcel.get("success").toString());
	    if(suceess){
	        String fileName=String.valueOf(exportExcel.get("data"));
	        redirect.addAttribute("fileName", fileName);
	        redirect.addAttribute("delete", true);
	        ModelAndView mv=new ModelAndView("redirect:/common/download");
	        return mv; 
	    }
	    return exportExcel;
    }
}
