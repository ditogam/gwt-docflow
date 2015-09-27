package com.docflow.server.db;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.docflow.shared.Report;
import com.docflow.shared.ReportHeaderItem;

public class ReportGenerator {
	private static String GENERATED_DIR = "tmpexcell";

	@SuppressWarnings("deprecation")
	public ReportGenerator(Report report, HttpServletRequest request,
			HttpServletResponse response, ServletContext context)
			throws Exception {

		String basePath = request.getServletPath();
		String realpath = context.getRealPath(basePath);
		File jspPath = new File(realpath);
		if (!(jspPath.exists() && jspPath.isFile()))
			return;
		File directory = jspPath.getParentFile();
		File excellDir = new File(directory, GENERATED_DIR);
		if (!excellDir.exists())
			excellDir.mkdir();
		File[] files = excellDir.listFiles();
		String filename = "Data_" + System.currentTimeMillis() + ".xls";
		for (File file : files) {
			file.delete();
		}
		File newFile = new File(excellDir, filename);
		FileOutputStream fos = new FileOutputStream(newFile);
		response.setHeader("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
		response.setDateHeader("Last-Modified", System.currentTimeMillis());
		response.setHeader("Pragma", "no-cache");

		response.setHeader("Content-disposition", "attachment;filename="
				+ filename);
		response.setContentType("application/vnd.ms-excel");
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(filename);
		HSSFHeader header = sheet.getHeader();
		header.setCenter(filename);

		HSSFFont boldFont = wb.createFont();
		boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		HSSFCellStyle boldStyle = wb.createCellStyle();
		boldStyle.setFont(boldFont);
		boldStyle.setWrapText(true);
		boldStyle.setBorderBottom((short) 2);
		boldStyle.setBorderTop((short) 2);
		boldStyle.setBorderLeft((short) 2);
		boldStyle.setBorderRight((short) 2);

		HSSFCellStyle cellStyle = wb.createCellStyle();

		cellStyle.setBorderBottom((short) 2);
		cellStyle.setBorderLeft((short) 2);
		cellStyle.setBorderRight((short) 2);
		int rowind = 0;
		HSSFRow row = sheet.createRow((short) rowind);
		ReportHeaderItem[] items = report.getHeaders();

		for (int i = 0; i < items.length; i++) {
			HSSFCell cell = row.createCell((short) i);

			int orientation = items[i].getOrientation();
			int width = items[i].getWidth();
			if (width > 0)
				sheet.setColumnWidth((short) i, (short) (width * 100));
			orientation = orientation > 0 ? orientation : ReportHeaderItem.LEFT;
			boldStyle.setAlignment((short) orientation);
			boldStyle.setVerticalAlignment((short) 2);
			cell.setCellStyle(boldStyle);
			cell.setCellValue(items[i].getTitle());
		}
		String[][] data = report.getData();
		rowind++;
		for (int i = 0; i < data.length; i++) {
			HSSFRow rowData = sheet.createRow(rowind++);
			String[] rowDataStr = data[i];
			for (int j = 0; j < rowDataStr.length; j++) {
				HSSFCell cell = rowData.createCell((short) j);
				int orientation = items[j].getOrientation();
				orientation = orientation > 0 ? orientation
						: ReportHeaderItem.LEFT;
				cellStyle.setAlignment((short) orientation);

				cell.setCellStyle(cellStyle);
				cell.setCellValue(rowDataStr[j]);
			}

		}
		wb.write(fos);
		fos.flush();
		fos.close();
		String redirectUrl = GENERATED_DIR + "/" + filename;
		response.sendRedirect(redirectUrl);
	}

	public void generate() {

	}
}
