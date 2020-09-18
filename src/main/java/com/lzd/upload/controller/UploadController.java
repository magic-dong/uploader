package com.lzd.upload.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lzd.upload.dao.FileResDao;
import com.lzd.upload.dao.FileTempResDao;
import com.lzd.upload.utils.DateUtils;
import com.lzd.upload.utils.file.FileRes;
import com.lzd.upload.utils.file.FileUtils;

@RestController
@RequestMapping("/file")
public class UploadController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	private FileResDao fileResDao;
	
	@Autowired
	private FileTempResDao fileTempResDao;
	
	@PostMapping("/isUpload")
	public Map<String, Object> isUpload(@RequestParam Map<String, Object> params) {
		String md5 = String.valueOf(params.get("filemd5"));
		// 查看大文件是否存在
		FileRes fi = new FileRes();
		fi.setMd5(md5);
		FileRes fileRes = fileResDao.selectByMd5(fi);
		return checkFileIsUpload(fileRes);
	}

	@PostMapping("/upload")
	public Map<String, Object> upload(@RequestParam Map<String, Object> params,@RequestParam(value="file",required=false)MultipartFile multipartFile) {
		// 根据action不同执行不同操作. check:校验分片是否上传过; upload:直接上传分片
		Map<String, Object> map = null;
		try {
			String action = String.valueOf(params.get("action"));
			String uuid = String.valueOf(params.get("uuid"));
			String fileName = String.valueOf(params.get("name"));
			Long size = Long.valueOf(String.valueOf(params.get("size")));// 总大小
			int total = Integer.valueOf(String.valueOf(params.get("total")));// 总片数
			int index = Integer.valueOf(String.valueOf(params.get("index")));// 当前是第几片
			String fileMd5 = String.valueOf(params.get("filemd5")); // 整个文件的md5
			String date = String.valueOf(params.get("date")); // 文件第一个分片上传的日期(如:20170122)
			String md5 = String.valueOf(params.get("md5")); // 分片的md5
			// 分片文件路径
			String saveDirectory = FileUtils.createPath(date, uuid);
			File path = new File(saveDirectory);
			if (!path.exists()) {
				path.mkdirs();
			}
			// 分片文件
			File file = new File(saveDirectory, uuid + "_" + index);
			if ("check".equals(action)) {
				logger.info("开始校验分片，当前校验第{}分片，该分片md5:{}!",index,md5);
				String md5Str = FileUtils.getFileMD5(file);
				// 已经上传部分文件 断点续传
				if (md5Str != null && md5Str.length() == 31) {
					//md5.js计算分片的MD5有时候跟后台不匹配
		            System.out.println("check length=" + md5.length() + " md5Str length=" + md5Str.length());
		            md5Str = "0" + md5Str;
		        }
				if (md5Str != null && md5Str.equals(md5)) {
					// 查询临时表 当前片段MD5是否存在
					FileRes shardFile = fileTempResDao.selectByShardMd5(md5, file.getPath());
					// 查看当前分片是否上传
					Map<String, Object> checkShardMap = checkShardFileIsUpload(shardFile);
					// 返回执行下一段
					return checkShardMap;
				} else {
					// 重新上传一次
					map = new HashMap<>();
					map.put("flag", "1");
					map.put("fileId", uuid);
					map.put("status", true);
					return map;
				}
			} else if ("upload".equals(action)) {
				logger.info("开始上传分片，当前上传第{}分片，该分片md5:{}!",index,md5);
				map = new HashMap<>();
				// 未上传
				// 删除文件后上传
				if (file.exists()) {
					file.delete();
				}
				multipartFile.transferTo(file);
				// 添加进数据库
				FileRes fileRes = new FileRes();
				fileRes.setUuid(uuid);
				fileRes.setName(fileName);
				fileRes.setPath(file.getPath());
				fileRes.setSize(multipartFile.getSize());
				fileRes.setMd5(md5);
				fileRes.setStatus(1);
				fileRes.setCreateTime(new Date());
				int success = fileTempResDao.insert(fileRes);
				if (success < 0) {// 片段失败重新上传
					map = new HashMap<>();
					map.put("flag", "4");
					map.put("fileId", uuid);
					map.put("status", false);
					return map;
				}
				// 文件第一个分片上传成功时大文件记录到数据库
				if (index == 1) {
					FileRes fi = new FileRes();
					fi.setMd5(fileMd5);
					//查询数据库是否已有记录
					FileRes md5File = fileResDao.selectByMd5(fi);
					String name = FileUtils.getFileNameNoSuffix(fileName);
					if (name.length() > 50) {
						name = name.substring(0, 50);
					}
					fi.setName(name);
					fi.setSuffix(FileUtils.getFileSuffixName(fileName));
					fi.setUuid(uuid);
					fi.setPath(FileUtils.createFolder(date) + File.separator + uuid + "." + fileRes.getSuffix());
					fi.setSize(size);
					fi.setStatus(0);
					if (md5File == null) {
						fileRes.setCreateTime(new Date());
						fileResDao.insert(fileRes);
					} else {
						fileRes.setUpdateTime(new Date());
						fileResDao.update(fileRes);
					}
				}
			}
			if (path.isDirectory()) {
				File[] shardFiles = path.listFiles();
				if (shardFiles != null) {
					//当临时数据分片表对应记录总数和本地分片文件数都等于，才合并
					//否则所有分片都校验一遍
					if (shardFiles.length == total) {
						int shardCount = fileTempResDao.selectShardCountByFileId(uuid);
						if(shardCount==total){
							// 分块全部上传完毕,合并
							String suffix = FileUtils.getFileSuffixName(fileName);
							File newFile = new File(FileUtils.createFolder(date), uuid + "." + suffix);
							boolean mergeFlag = FileUtils.mergeShardFileByMappedByteBuffer(newFile, shardFiles);
							System.out.println("大文件合并"+(mergeFlag?"成功!":"失败!"));
							// 修改FileRes记录为上传成功
							FileRes fileRes = new FileRes();
							fileRes.setUuid(uuid);
							fileRes.setName(fileName);
							fileRes.setStatus(1);
							fileRes.setMd5(fileMd5);
							fileRes.setPath(newFile.getPath());
							fileRes.setSize(newFile.length());
							fileRes.setSuffix(FileUtils.getFileSuffixName(fileName));
							int success = fileResDao.update(fileRes);
							map = new HashMap<>();
							map.put("fileId", uuid);
							map.put("flag", "5");
							if (success > 0) {
								map.put("size", fileRes.getSize());
								map.put("type", fileRes.getSuffix());
								map.put("status", true);
								map.put("path", fileRes.getPath());
							} else {
								map.put("status", false);
							}
							return map;
						}
					}
				}
			}
			//分片上传成功
			map = new HashMap<>();
			map.put("flag", "3");
			map.put("fileId", uuid);
			map.put("status", true);
			return map;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		}
		return map;
	}
	
	/**
	 * 校验大文件状态
	 * flag:1 文件未上传过 2：文件上传过部分，3：文件已经上传成功
	 * @author lzd
	 * @date 2020年4月14日:上午11:42:52
	 * @param fileRes
	 * @return
	 * @description
	 */
	private Map<String,Object> checkFileIsUpload(FileRes fileRes){
		Map<String, Object> map = null;
		if (fileRes == null ) {
			// 没有上传过文件
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			map = new HashMap<>();
			map.put("flag", "1");
			map.put("fileId", uuid);
			map.put("status", true);
			map.put("date", DateUtils.dateTime());
		} else {
			if (fileRes.getStatus() == 0) {
				// 文件上传部分
				map = new HashMap<>();
				map.put("flag", "2");
				map.put("fileId", fileRes.getUuid());
				map.put("status", true);
				map.put("date", DateUtils.dateTime());
			} else if (fileRes.getStatus() == 1) {
				// 文件已完成上传="秒传"
				map = new HashMap<>();
				map.put("flag", "3");
				map.put("fileId", fileRes.getUuid());
				map.put("path", fileRes.getPath());
				map.put("status", true);
				map.put("date", DateUtils.dateTime());
				map.put("size", fileRes.getSize());
				map.put("type", fileRes.getSuffix());
			}
		}
		return map;
	}
	
	/**
	 * 校验分片文件状态
	 * flag:1 没有上传过分片文件 2：分片文件上传部分(目前设计分片文件暂时不可能为0状态) 3：分片文件已上传过
	 * @author lzd
	 * @date 2020年4月14日:下午12:09:56
	 * @param shardFile
	 * @return
	 * @description
	 */
	private Map<String,Object> checkShardFileIsUpload(FileRes shardFile){
		Map<String, Object> map = null;
		if (shardFile == null ) {
			// 没有上传过分片文件
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			map = new HashMap<>();
			map.put("fileId", uuid);
			map.put("status", true);
			map.put("flag", "1");
			map.put("date", DateUtils.dateTime());
		} else {
			if (shardFile.getStatus() == 0) {
				// 分片文件上传部分(目前设计分片文件暂时不可能为0状态)
				map = new HashMap<>();
				map.put("flag", "2");
				map.put("fileId", shardFile.getUuid());
				map.put("status", true);
				map.put("date", DateUtils.dateTime());
			} else if (shardFile.getStatus() == 1) {
				// 分片文件已上传过
				map = new HashMap<>();
				map.put("flag", "3");
				map.put("fileId", shardFile.getUuid());
				map.put("status", true);
				map.put("date", DateUtils.dateTime());
			}
		}
		return map;
	}

	
	
}
