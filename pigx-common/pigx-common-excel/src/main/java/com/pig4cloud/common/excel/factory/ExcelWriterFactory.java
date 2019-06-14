package com.pig4cloud.common.excel.factory;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * excel工厂
 *
 * @author lishangbu
 * @date 2019-06-11
 */
public class ExcelWriterFactory extends ExcelWriter {
	private OutputStream outputStream;
	private int sheetNo = 1;

	public ExcelWriterFactory(OutputStream outputStream, ExcelTypeEnum typeEnum) {
		super(outputStream, typeEnum);
		this.outputStream = outputStream;
	}

	public ExcelWriterFactory write(List<? extends BaseRowModel> list, String sheetName,
									BaseRowModel object) {
		this.sheetNo++;
		try {
			Sheet sheet = new Sheet(sheetNo, 0, object.getClass());
			sheet.setSheetName(sheetName);
			this.write(list, sheet);
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				outputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	@Override
	public void finish() {
		super.finish();
		try {
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			//do something
		}
	}

	public void close() {
		try {
			outputStream.close();
		} catch (IOException e) {
			//do something
		}
	}
}
