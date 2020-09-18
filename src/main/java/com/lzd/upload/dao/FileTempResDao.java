package com.lzd.upload.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lzd.upload.utils.file.FileRes;

@Mapper
public interface FileTempResDao {
	
	FileRes selectByShardMd5(@Param("md5")String md5,@Param("path")String path);
	
	int selectShardCountByFileId(@Param("fileId")String fileId);
	
	int insert(FileRes fileRes);
	
	int update(FileRes fileRes);
}
