package mk.learning.fileshare.Services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ReadExcel {

	public static Map<String, Map<String, String>> setMapData(String Filename) throws IOException {
		DataFormatter formatter = new DataFormatter();
		String path = Filename;// "E:\testshare.xlsx";
		FileInputStream inputStream = new FileInputStream(path);
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheetAt(0);
		int LastRow = sheet.getLastRowNum();
		Map<String, Map<String, String>> ExcelMapData = new HashMap<String, Map<String, String>>();
		Map<String, String> MapData = new HashMap<String, String>();
		for (int i = 0; i <= LastRow; i++) {
			Row row = sheet.getRow(i);
			// Add key cell
			Cell keyCell = row.getCell(0);
			// Add value cell
			Cell valueCell = row.getCell(1);
			// Store data of value and key cell in string
			String key = Double.toString(keyCell.getNumericCellValue());
		String value = Double.toString(valueCell.getNumericCellValue());
			// Putting key, value to MapData
			MapData.put(key, value);
			// Putting data to ExcelMapData
			ExcelMapData.put("HR_REVISION", MapData);
		}
		// Return ExcelMap DATA
		return ExcelMapData;

	}

}
