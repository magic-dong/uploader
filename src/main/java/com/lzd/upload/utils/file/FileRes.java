package com.lzd.upload.utils.file;

import java.util.Date;

public class FileRes {

	private String uuid;
	private String name;
	private String path;
	private Long size;
	private String md5;
	private Integer status;
	private String suffix;
	private Date createTime;
	private Date updateTime;
	
	public FileRes(){
		
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "FileRes [uuid=" + uuid + ", name=" + name + ", path=" + path + ", size=" + size + ", md5=" + md5
				+ ", status=" + status + ", suffix=" + suffix + ", createTime=" + createTime + ", updateTime="
				+ updateTime + "]";
	}
	
}
