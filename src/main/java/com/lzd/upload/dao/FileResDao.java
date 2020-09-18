package com.lzd.upload.dao;

import org.apache.ibatis.annotations.Mapper;

import com.lzd.upload.utils.file.FileRes;

@Mapper
public interface FileResDao {
	
	FileRes selectByMd5(FileRes fileRes);
	
	int insert(FileRes fileRes);
	
	int update(FileRes fileRes);
}
