package mk.learning.fileshare.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import mk.learning.fileshare.Services.ReadExcel;
import mk.learning.fileshare.constants.HashmapConstants;

@Controller
public class AdminController {
	
	@Autowired
	ReadExcel excelRead;

	private final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@RequestMapping(value="/uploadFile", method=RequestMethod.GET)
	public String uploadGetController() {
		return "upload.html";
	}
	
	@RequestMapping(value="/uploadFile",method=RequestMethod.POST)
	public String uploadPostController(@RequestParam MultipartFile inputFile) throws IOException {
		Map<String, Map<String,String>> ResultMap=new HashMap<String,Map<String,String>>();
		ResultMap=ReadExcel.setMapData("E:\\testshare.xlsx");
		System.out.println(ResultMap);
		File uploadedFile = new File("uploadFile.txt");
		if(uploadedFile.exists())
			uploadedFile.delete();
		else
			uploadedFile.createNewFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile.getInputStream()));
		String line;
		StringBuilder inputFileContents = new StringBuilder();
		while((line=reader.readLine())!=null)
			inputFileContents.append(line);
		BufferedWriter writer = new BufferedWriter(new FileWriter(uploadedFile));
		System.out.println(inputFileContents);
		writer.write(inputFileContents.toString());
		writer.flush();
		writer.close();
		if(HashmapConstants.mapOfMaps==null)
			HashmapConstants.mapOfMaps = new HashMap<String,HashMap<String,String>>();
		HashMap<String,String> hrMap = HashmapConstants.mapOfMaps.get(HashmapConstants.HR_DETAILS);
		return "redirect:uploadFile?upload=success";
		
		
	}
	
}
