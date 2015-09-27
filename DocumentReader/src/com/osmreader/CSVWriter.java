package com.osmreader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class CSVWriter implements Serializable {
	private static final long serialVersionUID = 2444199148253681816L;

	public static final char COMMA_SEPARATOR = 0x2C;
	public static final char COLON_SEPARATOR = 0x3A;
	public static final char SEMICOLON_SEPARATOR = 0x3B;
	public static final char TABULATOR_SEPARATOR = 0x09;

	/**
	 * Writes a table model to csv formatted file
	 * 
	 * @param file
	 *            file to create
	 * @param model
	 *            model to write
	 * @throws IOException
	 */
	public void write(File file, CSVTableModel model, char separator)
			throws IOException {

	}

	protected String encodeValue(String value, char separator) {
		return "\"" + value + "\"";
	}

	public void write(File file, OSMPGReader model, char separator)
			throws IOException {
		/* create file */
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		String value;

		/* write columns */
		if (model.areColumnsVisible()) {
			for (int column = 0; column < model.getColumnCount(); column++) {
				value = encodeValue(model.getColumnName(column), separator);
				bw.write(value);
				if (column < model.getColumnCount() - 1) {
					bw.write(separator);
				} else {
					bw.newLine();
				}
			}
		}

		/* write data */
		for (int row = 0; row < model.getRowCount(); row++) {
			for (int column = 0; column < model.getColumnCount(); column++) {
				value = encodeValue(model.getValueAt(row, column), separator);
				bw.write(value);
				if (column < model.getColumnCount() - 1) {
					bw.write(separator);
				} else {
					bw.newLine();
				}
			}
		}

		/* close file */
		bw.close();

	}

	public static void main(String[] args) {
		Date dt=new Date();
		System.out.println(dt.getTime());
		String s = "";
		s += 97 + ":" + replaceChar(97) + ";";
		s += 98 + ":" + replaceChar(98) + ";";
		s += 103 + ":" + replaceChar(103) + ";";
		s += 100 + ":" + replaceChar(100) + ";";
		s += 101 + ":" + replaceChar(101) + ";";
		s += 118 + ":" + replaceChar(118) + ";";
		s += 122 + ":" + replaceChar(122) + ";";
		s += 84 + ":" + replaceChar(84) + ";";
		s += 105 + ":" + replaceChar(105) + ";";
		s += 107 + ":" + replaceChar(107) + ";";
		s += 108 + ":" + replaceChar(108) + ";";
		s += 109 + ":" + replaceChar(109) + ";";
		s += 110 + ":" + replaceChar(110) + ";";
		s += 111 + ":" + replaceChar(111) + ";";
		s += 112 + ":" + replaceChar(112) + ";";
		s += 74 + ":" + replaceChar(74) + ";";
		s += 114 + ":" + replaceChar(114) + ";";
		s += 115 + ":" + replaceChar(115) + ";";
		s += 116 + ":" + replaceChar(116) + ";";
		s += 117 + ":" + replaceChar(117) + ";";
		s += 102 + ":" + replaceChar(102) + ";";
		s += 113 + ":" + replaceChar(113) + ";";
		s += 82 + ":" + replaceChar(82) + ";";
		s += 121 + ":" + replaceChar(121) + ";";
		s += 83 + ":" + replaceChar(83) + ";";
		s += 67 + ":" + replaceChar(67) + ";";
		s += 99 + ":" + replaceChar(99) + ";";
		s += 90 + ":" + replaceChar(90) + ";";
		s += 119 + ":" + replaceChar(119) + ";";
		s += 87 + ":" + replaceChar(87) + ";";
		s += 120 + ":" + replaceChar(120) + ";";
		s += 106 + ":" + replaceChar(106) + ";";
		s += 104 + ":" + replaceChar(104) + ";";
System.out.println(s);
	}

	public static int replaceChar(int ch) {
		char newCode = (char) ch;
		switch (ch) {
		case 97:
			newCode = 'ა';
			break;
		case 98:
			newCode = 'ბ';
			break;
		case 103:
			newCode = 'გ';
			break;
		case 100:
			newCode = 'დ';
			break;
		case 101:
			newCode = 'ე';
			break;
		case 118:
			newCode = 'ვ';
			break;
		case 122:
			newCode = 'ზ';
			break;
		case 84:
			newCode = 'თ';
			break;
		case 105:
			newCode = 'ი';
			break;
		case 107:
			newCode = 'კ';
			break;
		case 108:
			newCode = 'ლ';
			break;
		case 109:
			newCode = 'მ';
			break;
		case 110:
			newCode = 'ნ';
			break;
		case 111:
			newCode = 'ო';
			break;
		case 112:
			newCode = 'პ';
			break;
		case 74:
			newCode = 'ჟ';
			break;
		case 114:
			newCode = 'რ';
			break;
		case 115:
			newCode = 'ს';
			break;
		case 116:
			newCode = 'ტ';
			break;
		case 117:
			newCode = 'უ';
			break;
		case 102:
			newCode = 'ფ';
			break;
		case 113:
			newCode = 'ქ';
			break;
		case 82:
			newCode = 'ღ';
			break;
		case 121:
			newCode = 'ყ';
			break;
		case 83:
			newCode = 'შ';
			break;
		case 67:
			newCode = 'ჩ';
			break;
		case 99:
			newCode = 'ც';
			break;
		case 90:
			newCode = 'ძ';
			break;
		case 119:
			newCode = 'წ';
			break;
		case 87:
			newCode = 'ჭ';
			break;
		case 120:
			newCode = 'ხ';
			break;
		case 106:
			newCode = 'ჯ';
			break;
		case 104:
			newCode = 'ჰ';
			break;

		}
		int k = (int) (newCode);
		return k;
	}
}

interface CSVTableModel {
	public String getValueAt(int row, int column);

	public int getColumnCount();

	public int getRowCount();

	public String getColumnName(int column);

	public boolean areColumnsVisible();
}
