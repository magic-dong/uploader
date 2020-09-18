package com.lzd.upload.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.lzd.upload.config.Global;

public class FileUtils {
	public final static String BASE_PATH=Global.getProfile();
	public final static String VIDEO_PATH="video";
	public static final String KEY_MD5 = "MD5";
	public static final String CHARSET_ISO88591 = "ISO-8859-1";
	
	public static void main(String[] args) {
		//1.19G文件117个分片
		String dirPath="F:\\test\\video\\20200415\\5a34203561444a2cb4e85485ab79f2c0";
		File file=new File(dirPath);
		File[] listFiles = file.listFiles();
		long start=System.currentTimeMillis();
		//39012ms=3.9s
		boolean flag = mergeShardFile("F:\\test\\video\\20200415","bb.zip",listFiles);
		//417ms
		//boolean flag = mergeShardFileByMappedByteBuffer("F:\\test\\video\\20200415","bb.zip",listFiles);
		System.out.println(flag);
		long end=System.currentTimeMillis();
		System.out.println(end-start);
	}
	
	
	/**
	 * 创建文件夹路径
	 * @author lzd
	 * @date 2020年4月14日:上午11:20:20
	 * @param date
	 * @return
	 * @description
	 */
	public static String createFolder(String date) {
		// 生成上传文件的路径信息，按天生成
		String saveDirectory = BASE_PATH + VIDEO_PATH + File.separator + date;
		// 验证路径是否存在，不存在则创建目录
		File path = new File(saveDirectory);
		if (!path.exists()) {
			path.mkdirs();
		}
		return saveDirectory;
	}
	
	/**
	 * 创建分片文件路径
	 * @author lzd
	 * @date 2020年4月14日:上午11:19:11
	 * @param date
	 * @param uuid
	 * @return
	 * @description
	 */
	public static String createPath(String date, String uuid) {
		// 生成上传文件的路径信息，按天生成
		String saveDirectory = BASE_PATH + VIDEO_PATH + File.separator + date
				+ File.separator + uuid;
		// 验证路径是否存在，不存在则创建目录
		File path = new File(saveDirectory);
		if (!path.exists()) {
			path.mkdirs();
		}
		return saveDirectory;
	}
	
	/**
	 * 获取文件的MD5
	 * @author lzd
	 * @date 2020年4月14日:上午11:22:00
	 * @param file
	 * @return
	 * @description
	 */
	@SuppressWarnings("resource")
	public static String getFileMD5(File file) {
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
			BigInteger bigInt = new BigInteger(1, digest.digest());
			return bigInt.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取文件的MD5
	 * @author lzd
	 * @date 2020年4月14日:上午11:23:01
	 * @param filePath
	 * @return
	 * @description
	 */
	public static String getFileMD5(String filePath) {
		File file = new File(filePath);
		return getFileMD5(file);
	}

	/**
	 * MD5 encrypt
	 * @author lzd
	 * @date 2020年4月14日:上午11:23:19
	 * @param data
	 * @return
	 * @throws Exception
	 * @description
	 */
	public static byte[] encryptMD5(byte[] data) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
		md5.update(data);
		return md5.digest();
	}

	/**
	 * MD5 encrypt
	 * @author lzd
	 * @date 2020年4月15日:上午11:14:05
	 * @param data
	 * @return
	 * @throws Exception
	 * @description
	 */
	public static byte[] encryptMD5(String data) throws Exception {
		return encryptMD5(data.getBytes(CHARSET_ISO88591));
	}

	/**
	 * 比较两个文件的Md5是否相同
	 * @author lzd
	 * @date 2020年4月14日:上午11:23:50
	 * @param file1
	 * @param file2
	 * @return
	 * @description
	 */
	public static boolean isSameMd5(File file1, File file2) {
		String md5_1 = FileUtils.getFileMD5(file1);
		String md5_2 = FileUtils.getFileMD5(file2);
		return md5_1.equals(md5_2);
	}

	/**
	 * 比较两个文件的Md5是否相同
	 * @author lzd
	 * @date 2020年4月14日:上午11:24:23
	 * @param filepath1
	 * @param filepath2
	 * @return
	 * @description
	 */
	public static boolean isSameMd5(String filePath1, String filePath2) {
		File file1 = new File(filePath1);
		File file2 = new File(filePath2);
		return isSameMd5(file1, file2);
	}
	
	/**
	 * 获取文件扩展名(后缀名)
	 * @author lzd
	 * @date 2020年4月14日:上午11:28:33
	 * @param fileName
	 * @return
	 * @description
	 */
	public static String getFileSuffixName(String fileName) {
		if ((fileName != null) && (fileName.length() > 0)) {
			int dot = fileName.lastIndexOf('.');
			if ((dot > -1) && (dot < (fileName.length() - 1))) {
				return fileName.substring(dot + 1);
			}
		}
		return fileName.toLowerCase();
	}

	/**
	 * 获取不带扩展名(后缀名)的文件名
	 * @author lzd
	 * @date 2020年4月14日:上午11:29:36
	 * @param filename
	 * @return
	 * @description
	 */
	public static String getFileNameNoSuffix(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename.toLowerCase();
	}
	
	/**
	 * 分片文件按后缀索引排序封装文件集合
	 * @author lzd
	 * @date 2020年4月15日:上午11:23:33
	 * @param shardFiles
	 * @return
	 * @description
	 */
	public static List<File> sortShardFileListByIndex(File...shardFiles){
		if(shardFiles==null || shardFiles.length==0){
			return null;
		}
		//转分片集合
		List<File> shardFileList = Arrays.asList(shardFiles);
		//先把分片文件xxx_1，xxx_2按索引序号排序
		Collections.sort(shardFileList, new Comparator<File>() {
			@Override
			public int compare(File file1, File file2) {
				// TODO Auto-generated method stub
				try {
					String shardFileName1 = file1.getName();
					String shardFileName2 = file2.getName();
					if(shardFileName1.lastIndexOf("_")==-1 || shardFileName2.lastIndexOf("_")==-1){
						return 0;
					}
					Integer index1 =Integer.valueOf(shardFileName1.substring(shardFileName1.lastIndexOf("_")+1));
					Integer index2 =Integer.valueOf(shardFileName2.substring(shardFileName2.lastIndexOf("_")+1));
					return index1-index2;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				return 0;
			}
		});	
		return shardFileList;
	}
	
	/**
	 * 分片文件数组合并成目标大文件
	 * @author lzd
	 * @date 2020年4月15日:下午4:20:25
	 * @param newFile 目标文件
	 * @param shardFiles 分片文件数组
	 * @return
	 * @description
	 */
	public static boolean mergeShardFile(File newFile,File...shardFiles){
		try {
			if(shardFiles==null || shardFiles.length==0){
				return false;
			}
			//目标文件夹
			File fileDir=newFile.getParentFile();
			if(!fileDir.exists()) fileDir.mkdir();
			//目标文件，已存在，删除原来的旧的，再合并
			if(newFile.exists()) newFile.delete();
			RandomAccessFile newRandomAccessFile=new RandomAccessFile(newFile,"rw");
			FileChannel newFileChannel = newRandomAccessFile.getChannel();
			//按分片文件后缀索引下标排序后再合并
			List<File> shardFileList = sortShardFileListByIndex(shardFiles);
			for (File shardFile : shardFileList) {
				FileInputStream fis = new FileInputStream(shardFile);
				FileChannel shardFileChannel = fis.getChannel();
				shardFileChannel.transferTo(0, shardFileChannel.size(), newFileChannel);
				shardFileChannel.close();
				fis.close();
			}
			newFileChannel.close();
			newRandomAccessFile.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 分片文件集合合并成目标大文件
	 * @author lzd
	 * @date 2020年4月15日:下午4:24:29
	 * @param newFile 目标文件
	 * @param shardFileList 分片文件集合
	 * @return
	 * @description
	 */
	public static boolean mergeShardFile(File newFile,List<File> shardFileList){
		File[] shardFiles=(File[]) shardFileList.toArray();
		return mergeShardFile(newFile,shardFiles);
	}
	
	/**
	 * 分片文件数组合并成目标大文件
	 * @author lzd
	 * @date 2020年4月15日:上午11:33:46
	 * @param fileDirPath
	 * @param fileName
	 * @param shardFiles
	 * @return
	 * @description
	 */
	public static boolean mergeShardFile(String fileDirPath,String fileName,File...shardFiles){
		File newFile=new File(fileDirPath,fileName);
		return mergeShardFile(newFile, shardFiles);
	}
	
	/**
	 * 分片文件集合合并成目标大文件
	 * @author lzd
	 * @date 2020年4月15日:上午11:34:23
	 * @param fileDirPath
	 * @param fileName
	 * @param shardFileList
	 * @return
	 * @description
	 */
	public static boolean mergeShardFile(String fileDirPath,String fileName,List<File> shardFileList){
		File newFile=new File(fileDirPath,fileName);
		return mergeShardFile(newFile,shardFileList);
	}
	
	/**
	 * 分片文件数组合并成目标大文件
	 * 利用MappedByteBuffer高效读写操作
	 * @author lzd
	 * @date 2020年4月15日:下午4:28:27
	 * @param newFile 目标文件
	 * @param shardFiles
	 * @return
	 * @description
	 */
	public static boolean mergeShardFileByMappedByteBuffer(File newFile,File...shardFiles){
		try {
			if(shardFiles==null || shardFiles.length==0){
				return false;
			}
			//目标文件夹
			File fileDir=newFile.getParentFile();
			if(!fileDir.exists()) fileDir.mkdir();
			//目标文件，已存在，删除原来的旧的，再合并
			if(newFile.exists()) newFile.delete();
			RandomAccessFile newRandomAccessFile=new RandomAccessFile(newFile,"rw");
			FileChannel newFileChannel = newRandomAccessFile.getChannel();
			//按分片文件后缀索引下标排序后再合并
			List<File> shardFileList = sortShardFileListByIndex(shardFiles);
			long offset=0;
			for (File shardFile : shardFileList) {
				RandomAccessFile shardFileRandomAccessFile=new RandomAccessFile(shardFile,"rw");
				FileChannel shardFileChannel = shardFileRandomAccessFile.getChannel();
				long size = shardFileChannel.size();
				newFileChannel.map(FileChannel.MapMode.READ_WRITE, offset, size);
				offset+=size;
				shardFileChannel.close();
				shardFileRandomAccessFile.close();
			}
			newFileChannel.close();
			newRandomAccessFile.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 分片文件集合合并成目标大文件
	 * 利用MappedByteBuffer高效读写操作
	 * @author lzd
	 * @date 2020年4月15日:下午4:28:27
	 * @param newFile 目标文件
	 * @param shardFiles
	 * @return
	 * @description
	 */
	public static boolean mergeShardFileByMappedByteBuffer(File newFile,List<File> shardFileList){
		File[] shardFiles=(File[]) shardFileList.toArray();
		return mergeShardFileByMappedByteBuffer(newFile, shardFiles);
	}
	
	/**
	 * 分片文件数组合并成目标大文件
	 * 利用MappedByteBuffer高效读写操作
	 * @author lzd
	 * @date 2020年4月15日:下午3:17:15
	 * @param fileDirPath
	 * @param fileName
	 * @param shardFiles
	 * @return
	 * @description
	 */
	public static boolean mergeShardFileByMappedByteBuffer(String fileDirPath,String fileName,File...shardFiles){
		File newFile=new File(fileDirPath,fileName);
		return mergeShardFileByMappedByteBuffer(newFile, shardFiles);
	}
	
	/**
	 * 分片文件集合合并成目标大文件
	 * 利用MappedByteBuffer高效读写操作
	 * @author lzd
	 * @date 2020年4月15日:下午3:17:15
	 * @param fileDirPath
	 * @param fileName
	 * @param shardFileList
	 * @return
	 * @description
	 */
	public static boolean mergeShardFileByMappedByteBuffer(String fileDirPath,String fileName,List<File> shardFileList){
		File newFile=new File(fileDirPath,fileName);
		return mergeShardFileByMappedByteBuffer(newFile, shardFileList);
	}
	
}
