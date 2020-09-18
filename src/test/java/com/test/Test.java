package com.test;

import com.lzd.upload.support.files.SiteFileFetch;
import com.lzd.upload.support.files.SiteFileInfoBean;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
//			SiteFileInfoBean bean=new SiteFileInfoBean("http://banzou.cdn.aliyun.com/apk/changba_6093.apk", "F:\\test", "aa.apk",5);
//			SiteFileFetchThread fileFetch=new SiteFileFetchThread(bean);
//			fileFetch.start();
			
//			SiteFileInfoBean bean=new SiteFileInfoBean("http://static.sse.com.cn/stock/information/c/201912/0c9ce253c5694e4ea43d8b461fbfcd7f.pdf", "F:\\test", "bb.pdf",5);
//			SiteFileFetch fileFetch=new SiteFileFetch(bean);
			
			SiteFileInfoBean bean=new SiteFileInfoBean("http://static.sse.com.cn/stock/information/c/201912/0c9ce253c5694e4ea43d8b461fbfcd7f.pdf", "F:\\test", "test.pdf",5);
			SiteFileFetch fileFetch=new SiteFileFetch(bean);
			fileFetch.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}	

}
