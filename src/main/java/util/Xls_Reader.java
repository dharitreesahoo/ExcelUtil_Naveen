package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Xls_Reader {
	public String path;
	public FileInputStream fis = null;
	public FileOutputStream fileOut = null;
	private XSSFWorkbook workbook = null;
	private XSSFSheet sheet = null;
	private XSSFRow row = null;
	private XSSFCell cell = null;

	public Xls_Reader(String path) {

		this.path = path;
		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheetAt(0);
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// returns the row count in a sheet

	public int getRowCount(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1)
			return 0;
		else {
			sheet = workbook.getSheetAt(index);
			int number = sheet.getLastRowNum() + 1;
			return number;
		}
	}

	// returns the data from a cell
	public String getCellData(String sheetName, String colName, int rowNum) {
		try {
			if (rowNum <= 0)
				return "";

			int index = workbook.getSheetIndex(sheetName);
			int col_Num = -1;
			if (index == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
					col_Num = i;
			}
			if (col_Num == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			cell = row.getCell(col_Num);

			if (cell == null)
				return "";

			// System.out.println(cell.getCellType().name());
			//
			if (cell.getCellType().name().equals("STRING"))
				return cell.getStringCellValue();

			// if (cell.getCellType().STRING != null)

			// if(cell.getCellType()==Xls_Reader.CELL_TYPE_STRING)
			// return cell.getStringCellValue();
			else if ((cell.getCellType().name().equals("NUMERIC")) || (cell.getCellType().name().equals("FORMULA"))) {

				String cellText = String.valueOf(cell.getNumericCellValue());
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// format in form of M/D/YY
					double d = cell.getNumericCellValue();

					Calendar cal = Calendar.getInstance();
					cal.setTime(HSSFDateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
					cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + 1 + "/" + cellText;

					// System.out.println(cellText);

				}

				return cellText;
			} else if (cell.getCellType().BLANK != null)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());

		} catch (Exception e) {

			e.printStackTrace();
			return "row " + rowNum + " or column " + colName + " does not exist in xls";
		}
	}

	// returns true if data is set successfully else false
	public boolean setCellData(String sheetName, String colName, int rowNum, String data) {
		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);

			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			int colNum = -1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equals(colName))
					colNum = i;
			}
			if (colNum == -1)
				return false;

			sheet.autoSizeColumn(colNum);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);

			cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			// cell style
			// CellStyle cs = workbook.createCellStyle();
			// cs.setWrapText(true);
			// cell.setCellStyle(cs);
			cell.setCellValue(data);

			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// returns true if sheet is created successfully else false
	public boolean addSheet(String sheetname) {

		FileOutputStream fileOut;
		try {
			workbook.createSheet(sheetname);
			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// returns true if sheet is removed successfully else false if sheet does
	// not exist
	public boolean removeSheet(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1)
			return false;

		FileOutputStream fileOut;
		try {
			workbook.removeSheetAt(index);
			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// returns true if column is created successfully
	public boolean addColumn(String sheetName, String colName) {
		// System.out.println("**************addColumn*********************");

		try {
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			int index = workbook.getSheetIndex(sheetName);
			if (index == -1)
				return false;

			XSSFCellStyle style = workbook.createCellStyle();
			// style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			// style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			if (row == null)
				row = sheet.createRow(0);

			// cell = row.getCell();
			// if (cell == null)
			// System.out.println(row.getLastCellNum());
			if (row.getLastCellNum() == -1)
				cell = row.createCell(0);
			else
				cell = row.createCell(row.getLastCellNum());

			cell.setCellValue(colName);
			cell.setCellStyle(style);

			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	// removes a column and all the contents
	public boolean removeColumn(String sheetName, int colNum) {
		try {
			if (!isSheetExist(sheetName))
				return false;
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet(sheetName);
			XSSFCellStyle style = workbook.createCellStyle();
			// style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			XSSFCreationHelper createHelper = workbook.getCreationHelper();
			// style.setFillPattern(XSSFCellStyle.NO_FILL);
			for (int i = 0; i < getRowCount(sheetName); i++) {
				row = sheet.getRow(i);
				if (row != null) {
					cell = row.getCell(colNum);
					if (cell != null) {
						cell.setCellStyle(style);
						row.removeCell(cell);
					}
				}
			}
			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// find whether sheets exists
	public boolean isSheetExist(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1) {
			index = workbook.getSheetIndex(sheetName.toUpperCase());
			if (index == -1)
				return false;
			else
				return true;
		} else
			return true;
	}

	// returns number of columns in a sheet
	public int getColumnCount(String sheetName) {
		// check if sheet exists
		if (!isSheetExist(sheetName))
			return -1;

		sheet = workbook.getSheet(sheetName);
		row = sheet.getRow(0);

		if (row == null)
			return -1;

		return row.getLastCellNum();

	}

	public int getCellRowNum(String sheetName, String colName, String cellValue) {

		for (int i = 2; i <= getRowCount(sheetName); i++) {
			if (getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
				return i;
			}
		}
		return -1;

	}

	public Object[][] getExcelDataBasedOnStartingPoint(String sheetName, String testName) {
		try {
			String dataSets[][] = null;
			/*FileInputStream file = new FileInputStream(new File(excellocation));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheet(sheetName);*/
			fis = new FileInputStream(path);
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet(sheetName);
			
		// count number of active rows
			int totalRow = sheet.getLastRowNum();
			int totalColumn = 0;
			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			int i = 0;
			int count = 1;
			while (rowIterator.hasNext() && count == 1 || count == 2) {
				// System.out.println(i);

				Row row = rowIterator.next();
				// For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				int j = 0;
				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();

					if (cell.getStringCellValue().contains(testName + "end")) {
						count = 0;
						break;
					}

					// System.out.println(sheetName+"Start");
					if (cell.getStringCellValue().contains(testName + "start")) {
						// count number of active columns in row
						totalColumn = row.getPhysicalNumberOfCells() - 1;
						// Create array of rows and column
						dataSets = new String[totalRow][totalColumn];
					}
					// System.out.println(sheetName+"Start");
					if (cell.getStringCellValue().contains(testName + "start") || count == 2) {
						System.out.println(sheetName + "start");
						count = 2;
						// Check the cell type and format accordingly

						switch (cell.getCellType().name().toUpperCase()) {
						case "NUMERIC":
							dataSets[i - 1][j++] = cell.getStringCellValue();
							System.out.println(cell.getNumericCellValue());
							break;
						case "STRING":
							if (!cell.getStringCellValue().contains(testName + "start")) {
								dataSets[i - 1][j++] = cell.getStringCellValue();
								System.out.println(cell.getStringCellValue());
							}
							break;
						case "BOOLEAN":
							dataSets[i - 1][j++] = cell.getStringCellValue();
							System.out.println(cell.getStringCellValue());
							break;
						case "FORMULA":
							dataSets[i - 1][j++] = cell.getStringCellValue();
							System.out.println(cell.getStringCellValue());
							break;
						}

					}
				}

				System.out.println("");
				i++;
			}
			///////file.close();

			return parseData(dataSets, totalColumn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method is used to remove unwanted null data from array
	 * 
	 * @param data
	 * @return
	 */
	public Object[][] parseData(Object[][] data, int colSize) {
		// Creating array list to store data;
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

		// This array list will store one Array index data, every array index
		// has three sets of data
		ArrayList<String> list1;

		System.out.println(data.length);

		// running for loop on array size
		for (int i = 0; i < data.length; i++) {
			// creates a list to store the elements != null

			System.out.println(data[i].length);

			list1 = new ArrayList<String>();
			// this for loop will run on array index, since each array index has
			// three sets of data
			for (int j = 0; j < data[i].length; j++) {
				// this if will check null
				if (data[i][j] != null) {
					list1.add((String) data[i][j]);
				}
			}
			// once all one array index data is entered in arrayList , then
			// putting this object in parent arrayList
			if (list1.size() > 0) {
				list.add(list1);
			}
		}
		// convert array List Data into 2D Array
		Object[][] arr2d = new Object[list.size()][colSize];
		// run loop on array list data
		for (int i = 0; i < list.size(); i++) {
			// every array list index has arryList inside
			ArrayList<String> t = list.get(i);
			// run loop on inner array List
			for (int j = 0; j < t.size(); j++) {
				arr2d[i][j] = t.get(j);
			}
		}
		System.out.println(list);
		System.out.println(arr2d);
		return arr2d;
	}

	public static void main(String[] args) {
		Xls_Reader reader = new Xls_Reader("d:/Test.xlsx");
		int rowCount = reader.getRowCount("Sheet1");
		System.out.println("No of row in excel " + rowCount);

		String cellData = reader.getCellData("Sheet1", "companyName", 4);
		System.out.println("particular cell data  in excel " + cellData);

		// Column no should be created before
		reader.setCellData("Sheet2", "newColumn", 3, "OmNamahSivaya");

		reader.addSheet("newSheet");

		reader.removeSheet("newSheet");

		reader.addColumn("Sheet1", "newColumn");

		reader.removeColumn("Sheet1", 3);

		reader.getColumnCount("Sheet1");

		int rowNumber = reader.getCellRowNum("Sheet1", "contactName", "dharitree");
		System.out.println("row number *******" + rowNumber);
		
		//String excellocation = "d:/demo.xlsx";
		String sheetName = "masterData";
		Xls_Reader excel = new Xls_Reader("d:/demo.xlsx");
		
		Object[][] data = excel.getExcelDataBasedOnStartingPoint(sheetName, "leadsCreation");
		
		System.out.println(data);
		
	}

	}


