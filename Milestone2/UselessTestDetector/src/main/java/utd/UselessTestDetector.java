package utd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public class UselessTestDetector {
	/**
	 * @param args
	 */
	public static Map<String, List<Integer>> hmap = new HashMap<String, List<Integer>>();
	public static void main(String[] args) {
		// Root directory for build reports ************   Change This **********
		//String rootDirectory="/home/harshal/Desktop/DevOps/milestone2/project/build-reports/";
		String rootDirectory="/var/lib/jenkins/build-reports/";
		// get list of all sub directories - test builds
		String[] testBuilds = getAllSubDirectories(rootDirectory);
		
		// print list of directories
		//System.out.println(Arrays.toString(testBuilds));
		int numberOfTestBuilds=testBuilds.length;
		//System.out.println(numberOfTestBuilds);
		//
		Map<String, List<Integer>> PassedTests = new HashMap<String, List<Integer>>();
		for(int j=0;j<numberOfTestBuilds;j++){
			String currentDirectory=rootDirectory+""+testBuilds[j];
			String xmlFilesList[]=getAllXmlFileList(currentDirectory);
			for(int k=0;k<xmlFilesList.length;k++){
				String filename=rootDirectory+testBuilds[j]+"/"+xmlFilesList[k];
				//System.out.println(filename);
				PassedTests= getListOfTestCase(testBuilds[j],filename);
			}
		}
		int numberOfUselessTests=0;
		//Right to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("/var/lib/jenkins/UselessTests.txt", "UTF-8");
			//writer = new PrintWriter("/home/harshal/Desktop/UselessTests.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		writer.println("Useless Tests :");
		for(String s : PassedTests.keySet()) {
	       	if(PassedTests.get(s).size()==numberOfTestBuilds){
	       		writer.println(s);	
	       		numberOfUselessTests++;
	       	}
		}
		writer.println("Total Number of Useless Tests:"+numberOfUselessTests);
		writer.close();
		//close output file
	}
	/*
	 * function to return list of all xml files
	*/
	private static String[] getAllXmlFileList(String currentDirectory) {
		// TODO Auto-generated method stub
		File folder = new File(currentDirectory);
		List<String> xmlFileList = new ArrayList<String>();
		File[] listOfFiles = folder.listFiles();
		String xmlFiles[];
		for(int i = 0; i < listOfFiles.length; i++){
			String filename = listOfFiles[i].getName();
			if(filename.endsWith(".xml")||filename.endsWith(".XML")){
				xmlFileList.add(filename);
			}
		}
		xmlFiles=xmlFileList.toArray(new String[xmlFileList.size()]);
		
		return xmlFiles;
	}
	// function to get list of all directories
	private static String[] getAllSubDirectories(String rootDirectory) {
		// TODO Auto-generated method stub
		File file = new File(rootDirectory);
		String[] subDirectories= file.list(new FilenameFilter() {
			  public boolean accept(File current, String name) {
				    return new File(current, name).isDirectory();
				  }
				});
		return subDirectories;
	}
	/*
	 * function which returns Map of test cases and list of build numbers in which it passed
	*/
	private static Map<String, List<Integer>> getListOfTestCase(String testBuild, String filename) {
		int testBuildNumber=Integer.parseInt(testBuild);
		// TODO Auto-generated method stub
		try {
			/*
			 * Retrieve test cases from XML file with no errors
			*/
			File inputfile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(inputfile);
	        doc.getDocumentElement().normalize();
	        Element docElement = (Element)doc.getDocumentElement();
	        String testsuite = docElement.getAttribute("name");
	        NodeList nList = doc.getElementsByTagName("testcase");	         
	        for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);	            
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	               Element eElement = (Element) nNode;
            	   String testcasename = eElement.getAttribute("name");
            	   String key = testsuite + "_" + testcasename;
            	   // get passed test case
	               if(eElement.getElementsByTagName("error").getLength() == 0) {
	            	   	List<Integer> list =hmap.getOrDefault(key, new ArrayList<Integer>());
	            	   	list.add(testBuildNumber);
	            	   	hmap.put(key, list);
	               } else {
	            	   	List<Integer> list =hmap.getOrDefault(key, new ArrayList<Integer>());
	            	   	hmap.put(key, list);
	               }	               
	           } 
	         } 
	         
		} catch(Exception e) {
			System.out.println(e);
		}
		return hmap;
	}

}
