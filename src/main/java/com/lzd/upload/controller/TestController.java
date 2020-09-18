package com.lzd.upload.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lzd.upload.controller.BaseController;
import com.lzd.upload.dao.TestDao;
import com.lzd.upload.json.MessageResult;

@RestController
@RequestMapping("/test")
public class TestController extends BaseController {
	private static final Logger logger=LoggerFactory.getLogger(TestController.class);
			
	@Autowired
	private TestDao testDao;
	
	@GetMapping("/upload")
	public MessageResult upload(@RequestParam Map<String, Object> params) {
		try {
			List<Map<String, Object>> list = testDao.select();
			return success(list);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			return error("error!");
		}
	}
	
	
}
