package com.lzd.upload.support.files;

import java.io.Serializable;

/**
 * 网络文件对象
 * @author lzd
 * @date 2020年2月21日
 * @version
 */
public class SiteFileInfoBean implements Serializable{
	private static final long serialVersionUID = -5406357699786745688L;

	/**
     * 文件URL资源
     */
    private String sSiteURL; // Site's URL

    /**
     * 文件保存的路径(不包含文件名)
     */
    private String sFilePath; // Saved File's Path

    /**
     * 保存的文件名
     */
    private String sFileName; // Saved File's Name

    /**
     * 下载线程个数
     */
    private int nSplitter;

    public SiteFileInfoBean()
    {
        // default value of nSplitter is 4
        this("", "", "", 4);
    }

    /**
     *
     * @param sURL 文件资源URL
     * @param sPath 文件保存的路径(不包含文件名)
     * @param sName 文件名
     * @param nSpiltter 下载线程个数
     */
    public SiteFileInfoBean(String sURL, String sPath, String sName, int nSpiltter)
    {
        sSiteURL = sURL;
        sFilePath = sPath;
        sFileName = sName;
        this.nSplitter = nSpiltter;
    }

    public String getSSiteURL()
    {
        return sSiteURL;
    }

    public void setSSiteURL(String value)
    {
        sSiteURL = value;
    }

    /**
     * 获取文件保存的路径
     * @return
     */
    public String getSFilePath()
    {
        return sFilePath;
    }

    public void setSFilePath(String value)
    {
        sFilePath = value;
    }

    /**
     * 获取文件名
     * @return
     */
    public String getSFileName()
    {
        return sFileName;
    }

    public void setSFileName(String value)
    {
        sFileName = value;
    }

    /**
     * 分割成的子文件个数
     * @return
     */
    public int getNSplitter()
    {
        return nSplitter;
    }

    public void setNSplitter(int nCount)
    {
        nSplitter = nCount;
    }
}
