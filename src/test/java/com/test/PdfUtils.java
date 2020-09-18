package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class PdfUtils {
	public static void main(String[] args) {
		String pdfPath = "F:\\test\\bb.pdf";
		long start = System.currentTimeMillis();
		String readPdfToTxt = readPdfToTxt(pdfPath);
		System.out.println(readPdfToTxt);
		// convertText(pdfPath);
		long end = System.currentTimeMillis();
		System.out.println("总耗时：" + (end - start) / 1000 + "秒!");
	}

	public static void readPdfToTxt(String pdfPath, String txtfilePath) {
		// 读取pdf所使用的输出流
		PrintWriter writer = null;
		PdfReader reader = null;
		try {
			writer = new PrintWriter(new FileOutputStream(txtfilePath));
			reader = new PdfReader(pdfPath);
			int totalPage = reader.getNumberOfPages();// 获得页数
			System.out.println("Total Page: " + totalPage);
			String content = ""; // 存放读取出的文档内容
			for (int i = 1; i <= totalPage; i++) {
				// 读取第i页的文档内容
				content += PdfTextExtractor.getTextFromPage(reader, i);
			}
			String[] arr = content.split("/n");
			for (int i = 0; i < arr.length; i++) {
				System.out.println(arr[i]);
			}
			writer.write(content);// 写入文件内容

			writer.flush();

			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static String readPdfToTxt(String pdfPath) {
		PdfReader reader = null;
		StringBuffer buff = new StringBuffer();
		try {
			// FileInputStream fis = new FileInputStream(pdfPath);
			// byte[] b = new byte[1024];
			// int length = 0;
			// while ((length = fis.read(b)) != -1) {
			// // fis.read(b, 0,length);
			// reader = new PdfReader(b);
			// PdfReaderContentParser parser = new
			// PdfReaderContentParser(reader);
			// int totalPage = reader.getNumberOfPages();// 获得页数
			// TextExtractionStrategy strategy = null;
			// for (int i = 1; i <= totalPage; i++) {
			// strategy = parser.processContent(i, new
			// SimpleTextExtractionStrategy());
			// buff.append(strategy.getResultantText());
			// }
			// }

			reader = new PdfReader(pdfPath);
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			int totalPage = reader.getNumberOfPages();// 获得页数
			TextExtractionStrategy strategy = null;
			for (int i = 1; i <= totalPage; i++) {
				strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
				buff.append(strategy.getResultantText());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return buff.toString();
	}

	public static void convertText(String pdfPath) {
		PDDocument doc = null;
		// OutputStream fos = null;
		// Writer writer = null;
		PDFTextStripper stripper = null;
		// StringBuffer sbf = new StringBuffer();
		try {
			// doc = PDDocument.load(new File(pdfPath));
			doc = PDDocument.load(new File(pdfPath), MemoryUsageSetting.setupTempFileOnly());
//			fos = new FileOutputStream(pdfPath.substring(0, pdfPath.indexOf(".")) + ".doc");
//			writer = new OutputStreamWriter(fos, "UTF-8");
			stripper = new PDFTextStripper();
			int pageNumber = doc.getNumberOfPages();
			stripper.setSortByPosition(true);
//			for (int i = 1; 2 * i - 1 <= pageNumber; i++) {
//				stripper.setStartPage(2 * i - 1);
//				stripper.setEndPage(Math.min(2 * i, pageNumber));
//				String content = stripper.getText(doc);
//				System.out.println(content);
//				sbf.append(content);
//			}
			stripper.setStartPage(1);
			stripper.setEndPage(pageNumber);
			String content = stripper.getText(doc);
			System.out.println(content);
			// stripper.writeText(doc, writer);
			// writer.close();
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return sbf.toString();
	}

}
