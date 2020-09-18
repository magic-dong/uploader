package com.lzd.upload.config;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzd.upload.utils.StringUtils;
import com.lzd.upload.utils.YamlUtils;

/**
 * 全局配置类
 * @author lzd
 * @date 2019年7月25日
 * @version
 */
public class Global
{
    private static final Logger logger = LoggerFactory.getLogger(Global.class);

    private static String NAME = "application.yml";

    /**
     * 当前对象实例
     */
    private static Global global = null;

    /**
     * 保存全局属性值
     */
    private static Map<String, String> map = new HashMap<String, String>();

    private Global()
    {
    }

    /**
     * 静态工厂方法 获取当前对象实例 多线程安全单例模式(使用双重同步锁)
     */

    public static synchronized Global getInstance()
    {
        if (global == null)
        {
            synchronized (Global.class)
            {
                if (global == null)
                    global = new Global();
            }
        }
        return global;
    }

    /**
     * 获取配置
     */
    public static String getConfig(String key)
    {
        String value = map.get(key);
        if (StringUtils.isNull(value))
        {
            //Map<?, ?> yamlMap = null;
            try
            {
                //yamlMap = YamlUtils.loadYaml(NAME);
                //value = String.valueOf(YamlUtils.getProperty(yamlMap, key));
            	//获取yml中配置文件属性（包含从active文件中匹配）
                value=getYamlTargetValue(key);
                map.put(key, StringUtils.isNotNull(value)? value : StringUtils.EMPTY);
            }
            catch (FileNotFoundException e)
            {
            	logger.error("获取全局配置异常 {}", key);
            }
        }
        return value;
    }
    
    /**
     * 从yml配置文件中获取key配置值（包含从active文件中匹配）
     * @author lzd
     * @date 2019年9月26日:下午3:46:33
     * @param key
     * @return
     * @throws FileNotFoundException
     * @description
     */
    private static String getYamlTargetValue(String key) throws FileNotFoundException{
    	Map<?, ?> yamlMap  = YamlUtils.loadYaml(NAME);
		String value = String.valueOf(YamlUtils.getProperty(yamlMap, key));
    	if(StringUtils.isNull(value)){
    		String actives=String.valueOf(YamlUtils.getProperty(yamlMap, "spring.profiles.active"));
    		if(StringUtils.isNotNull(actives)){
    			String[] ymlSuffixArray = actives.trim().split(",");
    			for (String ymlSuffix : ymlSuffixArray) {
    				ymlSuffix=ymlSuffix.trim();
    				String activeYml=NAME.substring(0, NAME.lastIndexOf("."))+"-"+ymlSuffix+NAME.substring(NAME.lastIndexOf("."));
    				yamlMap  = YamlUtils.loadYaml(activeYml);
    				value = String.valueOf(YamlUtils.getProperty(yamlMap, key));
    				if(StringUtils.isNotNull(value)) break;
				}
    		}
        }
        return value;
    }

    /**
     * 获取项目名称
     */
    public static String getName()
    {
        return StringUtils.nvl(getConfig("project.name"), "File-Upload");
    }

    /**
     * 获取项目版本
     */
    public static String getVersion()
    {
        return StringUtils.nvl(getConfig("project.version"), "1.0.0");
    }

    /**
     * 获取版权年份
     */
    public static String getCopyrightYear()
    {
        return StringUtils.nvl(getConfig("project.copyrightYear"), "2020");
    }


    /**
     * 获取文件上传路径
     */
    public static String getProfile()
    {
        return getConfig("project.profile");
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath()
    {
        return getProfile() + "avatar/";
    }

    /**
     * 获取下载上传路径
     */
    public static String getDownloadPath()
    {
        return getProfile()+ "download/";
    }


  
}
