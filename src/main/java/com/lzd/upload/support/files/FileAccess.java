package com.lzd.upload.support.files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class FileAccess implements Serializable {

	private static final long serialVersionUID = -4841321594454312948L;
	
	private RandomAccessFile savedFile;
	private long nPos;
	
	public FileAccess(String sName, long nPos) throws IOException{
		super();
		this.savedFile = new RandomAccessFile(sName, "rw");
		this.nPos = nPos;
		savedFile.seek(this.nPos);
	}
	
	public int write(byte[] b, int nStart, int nLen)
    {
        int n = -1;
        try
        {
        	savedFile.write(b, nStart, nLen);
            n = nLen;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return n;
    }
	
}
