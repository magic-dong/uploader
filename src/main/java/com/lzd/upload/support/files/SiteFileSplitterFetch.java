package com.lzd.upload.support.files;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SiteFileSplitterFetch extends Thread {
	private String sURL; // File URL
	private long nStartPos; // File Snippet Start Position
	private long nEndPos; // File Snippet End Position
	private int nThreadID; // Thread's ID
	private boolean bDownOver = false; // Downing is over
	private boolean bStop = false; // Stop identical
	private FileAccess fileAccess = null; // File Access interface

	/**
	 *
	 * @param sURL 文件资源URL
	 * @param sName 要保存的文件名(完整路径,绝对路径)
	 * @param nStart 文件指针开始位置
	 * @param nEnd 文件指针结束位置
	 * @param id 线程ID
	 * @throws IOException
	 */
	public SiteFileSplitterFetch(String sURL, String sName, long nStart, long nEnd, int id) throws IOException {
		this.sURL = sURL;
		this.nStartPos = nStart;
		this.nEndPos = nEnd;
		nThreadID = id;
		fileAccess = new FileAccess(sName, nStartPos);
	}

	public void run() {
		while (nStartPos < nEndPos && !bStop) {
			try {
				URL url = new URL(sURL);

				HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestProperty("User-Agent", "NetFox");

				String sProperty = "bytes=" + nStartPos + "-";
				httpConnection.setRequestProperty("RANGE", sProperty);

//				System.out.println(sProperty);

				InputStream input = httpConnection.getInputStream();

				byte[] b = new byte[1024];

				int nRead;
				long startPos=nStartPos;
				while ((nRead = input.read(b, 0, 1024)) > 0 && nStartPos < nEndPos && !bStop) {
					// 注意这里不用再判断 nRead+nStartPos<nEndPos,只需要nStartPos<nEndPos就可以,
					// 因为是前面几个下载线程读取的内容超出了nEndPos,也会被它后面的子线程读取内容覆盖掉,
					// 最后一个子下载子线程最后读取到的字节个数小于1024的,所以总的结束位置不超过就可以
					nStartPos += fileAccess.write(b, 0, nRead);
				}

				System.out.println( "Thread " + nThreadID + " is over!" + ",nStartPos=" + startPos + ",nEndPos=" + nEndPos);
				bDownOver = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void splitterStop() {
		bStop = true;
	}

	public String getsURL() {
		return sURL;
	}

	public void setsURL(String sURL) {
		this.sURL = sURL;
	}

	public long getnStartPos() {
		return nStartPos;
	}

	public void setnStartPos(long nStartPos) {
		this.nStartPos = nStartPos;
	}

	public long getnEndPos() {
		return nEndPos;
	}

	public void setnEndPos(long nEndPos) {
		this.nEndPos = nEndPos;
	}

	public int getnThreadID() {
		return nThreadID;
	}

	public void setnThreadID(int nThreadID) {
		this.nThreadID = nThreadID;
	}

	public boolean isbDownOver() {
		return bDownOver;
	}

	public void setbDownOver(boolean bDownOver) {
		this.bDownOver = bDownOver;
	}

	public boolean isbStop() {
		return bStop;
	}

	public void setbStop(boolean bStop) {
		this.bStop = bStop;
	}

	public FileAccess getFileAccess() {
		return fileAccess;
	}

	public void setFileAccess(FileAccess fileAccess) {
		this.fileAccess = fileAccess;
	}
}
