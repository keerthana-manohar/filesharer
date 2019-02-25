package mk.learning.fileshare.Services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.qos.logback.core.net.SyslogOutputStream;
import mk.learning.fileshare.constants.HashmapConstants;

@Component
public class SearchFileBasedOnEmp {
	private final Logger logger = LoggerFactory.getLogger(SearchFileBasedOnEmp.class);

	/*
	 * public File searchPath(File file, String search) { // file=new File("E:\\");
	 * // search="1626285.pdf"; if (file.isDirectory()) { File[] files =
	 * file.listFiles(); String[] filePath = new String[10000]; for (File f : files)
	 * { File found = searchPath(f, search); if (found != null) {
	 * filePath[0]=file.getAbsolutePath(); return found; } }
	 * 
	 * } else { if(file.getName().equals(search)) { logger.info("else part"); return
	 * file; }
	 * 
	 * } return null;
	 * 
	 * }
	 */

	@Value("${downloadBaseDir}")
	String downloadBaseDir;

	// @Value("${pathDelimiter}")
	// String pathDelimiter;

	@Value("${hrDir:HR}")
	String hrDir;

	@Value("${finDir:FINANCE}")
	String finDir;

	@Value("${tempDir:temp}")
	String tempDir;

	String pathDelimiter = File.separator;

	@Autowired
	FileServices delTempFile;

	public ArrayList<String> FindAllEmpForHR(String hrCode)

	{
		ArrayList<String> EmpListForHR = new ArrayList<>();
		Map<String, String> empHrMap = HashmapConstants.mapOfMaps.get(HashmapConstants.HR_DETAILS);
		for (String entry : empHrMap.keySet()) {
			String curHr = empHrMap.get(entry);
			logger.info("Cur HR: {}", curHr);
			if (curHr.equalsIgnoreCase(hrCode)) {
				logger.info("In IF");
				EmpListForHR.add(entry);
			}
		}
		logger.info("Emp_list for HR = {}", EmpListForHR);
		return EmpListForHR;
	}
	
	// we need a function that will return a list of paths to files related to given
	// employee and functionality
	// this function needs to be modified to take list of employee codes
	public ArrayList<String> getFilePaths4Employee(ArrayList<String> empCode, String functionality) {
		String baseDir;

		if (functionality.equalsIgnoreCase(HashmapConstants.FUNCTIONALITY_HR))
			baseDir = downloadBaseDir + pathDelimiter + hrDir;
		else if (functionality.equalsIgnoreCase(HashmapConstants.FUNCTIONALITY_FIN))
			baseDir = downloadBaseDir + pathDelimiter + finDir;
		else
			return null;

		logger.info("baseDir = {}", baseDir);

		File baseDirFile = new File(baseDir);
		if (!baseDirFile.isDirectory()) {
			logger.info("baseDir is not a directory");
			return null;
		} else {
			logger.info("baseDir is a directory, traversing");
			return findAllEmployees(baseDirFile, empCode);
		}
	}

	private ArrayList<String> findAllEmployees(File baseDirFile, ArrayList<String> empCode) {
		ArrayList<String> result = new ArrayList<String>();

		traverse(baseDirFile, result, empCode);
		return result;
	}

	private void traverse(File baseDirFile, ArrayList<String> result, ArrayList<String> empCodeList) {
		logger.info("traversing file {}", baseDirFile.getAbsolutePath());
		if (baseDirFile.isDirectory()) {
			String[] baseDirFileChildren = baseDirFile.list();
			for (int i = 0; i < baseDirFileChildren.length; i++) {
				logger.info("calling traverse for {}", baseDirFileChildren[i]);
				traverse(new File(baseDirFile.getAbsoluteFile() + pathDelimiter + baseDirFileChildren[i]), result,
						empCodeList);
			}
		} else {
			for (String empCode : empCodeList) {
				String paddedEmpCodeWithExt = HashmapConstants.zeroString.substring(0, 8 - empCode.length()) + empCode
						+ HashmapConstants.PDF_EXTENSION;
				logger.info("paddedEmpCodeWithExt = {}", paddedEmpCodeWithExt);
				logger.info("in else -- {}", baseDirFile.getName());
				logger.info("Path Delimiter={}", pathDelimiter);
				if (baseDirFile.getName().contains(paddedEmpCodeWithExt)) {
					logger.info("adding File {} to list", baseDirFile.getAbsolutePath());
					result.add(baseDirFile.getAbsolutePath());
				}
			}
		}
	}

	// we need a function, which will form a zip of all the files
	public String zipFiles(ArrayList<String> filePaths) throws IOException {
		// cleanTempFiles
		delTempFile.cleanTempFiles(tempDir);
		String tempZipFilename = tempDir + "\\" + new Date().getTime() + ".zip";
		logger.info("tempZipFilename={}", tempZipFilename);
		File tempZipFile = new File(tempZipFilename);
		if (tempZipFile.exists() == false)
			tempZipFile.createNewFile();
		ZipOutputStream zipOpStream = new ZipOutputStream(new FileOutputStream(tempZipFile));
		for (String curFile : filePaths) {
			String[] curFileSplitNames = curFile.split("\\\\");
			logger.info("curFile={}", curFile);
			String zipEntryName = "";
			int index = 0;
			for (int i = 0; i < curFileSplitNames.length; i++) {
				if (curFileSplitNames[i].equalsIgnoreCase("downloads"))
					index = i;
			}
			for (int i = index + 1; i < curFileSplitNames.length; i++) {
				zipEntryName = zipEntryName + "\\" + curFileSplitNames[i];
			}
			if (zipEntryName.equalsIgnoreCase(""))
				zipEntryName = curFile;
			else
				zipEntryName = zipEntryName.substring(1);
			logger.info("current zip entry = {}", zipEntryName);
			logger.info("CurFile = {}", curFile);
			ZipEntry ze = new ZipEntry(zipEntryName);

			byte[] fileBytes = Files.readAllBytes(Paths.get(curFile));
			zipOpStream.putNextEntry(ze);
			zipOpStream.write(fileBytes, 0, fileBytes.length);
			zipOpStream.flush();
			zipOpStream.closeEntry();

		}
		zipOpStream.close();
		return tempZipFilename;
	}
}