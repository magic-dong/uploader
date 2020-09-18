package com.lzd.upload.json;

import java.util.LinkedHashMap;

/**
 * 操作消息提醒
 * 
 *@author lzd
 */
public class MessageResult extends LinkedHashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /**
     * 初始化一个新创建的 Message 对象
     */
    public MessageResult()
    {
    }

    /**
     * 返回错误消息
     * 
     * @return 错误消息
     */
    public static MessageResult error()
    {
        return error(500, "操作失败");
    }

    /**
     * 返回错误消息
     * 
     * @param msg 内容
     * @return 错误消息
     */
    public static MessageResult error(String msg)
    {
        return error(200, msg);
    }

    /**
     * 返回错误消息
     * 
     * @param code 错误码
     * @param msg 内容
     * @return 错误消息
     */
    public static MessageResult error(int code, String msg)
    {
        MessageResult json = new MessageResult();
        json.put("code", code);
        json.put("success", false);
        json.put("error", msg);
        return json;
    }

    
    /**
     * 返回成功消息
     * @author lzd
     * @date 2019年6月19日:下午3:10:29
     * @param data
     * @return
     * @description
     */
    public static MessageResult success(Object data){
    	MessageResult json = new MessageResult();
        json.put("code", 200);
        json.put("success", true);
        json.put("data", data);
        return json;
    }
    
    /**
     * 返回成功消息
     * 
     * @return 成功消息
     */
    public static MessageResult success()
    {
        return MessageResult.success("操作成功");
    }

    /**
     * 返回成功消息
     * 
     * @param key 键值
     * @param value 内容
     * @return 成功消息
     */
    @Override
    public MessageResult put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }
}
