package com.tihor.ocr.domain;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.tihor.ocr.dao.ArangoDao;
import com.tihor.ocr.utils.EdsConstants;

/**
 * Hello world!
 *
 */


public class App {
	static ArangoDao aDao=new ArangoDao();
	
	public static void main1(String[] args) throws Exception {
		System.out.println("Hello World!");
		File f = new File("E:/OCR/AC0250001.pdf");
		/*
		 * InputStream f = new FileInputStream(f); BufferedInputStream br=new
		 * BufferedInputStream(f);
		 */
		PDDocument document = PDDocument.load(f);
		Long count = 0L;
		COSDocument doc = document.getDocument();
		int noOfPages = document.getNumberOfPages();
		List listm = new ArrayList<Model>();
		for (int i = noOfPages-3; i < noOfPages; i++) {
			PDFTextStripper textStripper = new PDFTextStripper();
			textStripper.setStartPage(i);
			textStripper.setEndPage(i + 1);
			String s = textStripper.getText(document);
			int startIndex = s.indexOf("Sex", 0);
			int endIndex = 0;
			System.out.println(s);
			extractMetaData(s);
			do {
				endIndex = s.indexOf("Sex", startIndex + 1);
				if (endIndex > 0) {
					String block = s.substring(startIndex, endIndex);
					Model m = new Model();
					m.fill(block);
					listm.add(m);
					startIndex = endIndex;
				}
			} while (endIndex < s.length() && endIndex >= 0);
		}
		System.out.println(Arrays.toString(listm.toArray()));
		System.out.println("The number of records processed : " + count);
	}

	public static Long process() throws IOException {

		System.out.println("Hello World!");
		File f = new File("E:\\EDS\\InputData\\AC0020126.pdf");
		/*  
		 * InputStream f = new FileInputStream(f); BufferedInputStream br=new
		 * BufferedInputStream(f);
		 */
		PDDocument document = PDDocument.load(f);
		Long count = 0L;
		COSDocument doc = document.getDocument();
		int noOfPages = document.getNumberOfPages();
		List listm = new ArrayList<Model>();

		for (int i = 3; i < noOfPages; i++) {
			PDFTextStripper textStripper = new PDFTextStripper();
			//textStripper.setSortByPosition(true);
			textStripper.setStartPage(i);
			textStripper.setEndPage(i);
			String s= textStripper.getText(document);
			//to handle suppliments
			String pAdditions="(?s)(Supplement)";
			Pattern rAdditions=Pattern.compile(pAdditions);
			Matcher mAdditions=rAdditions.matcher(s);
			if(mAdditions.find()){
				handleSuppliments(document,i);
				break;
			}
			Map<String ,String> metaDataMap = extractMetaData(s);
			int startIndex = s.indexOf("Sex", 0);
			int endIndex = 0;
			System.out.println(s);
			do {
				endIndex = s.indexOf("Sex", startIndex + 1);
				if(endIndex==-1){
					endIndex=s.length()-1;
				}
				if (endIndex >0) {
					String block = s.substring(startIndex, endIndex);
					Model m = new Model();
					try {
						m.setSectionNo(Integer.parseInt(metaDataMap.get("sectionNo")));
						m.setSectionName(metaDataMap.get("sectionName"));
						m.fill(block);
					} catch (Exception e) {
						//
						e.printStackTrace();
					}
					listm.add(m);
					count++;
					if (m.getSno() == 29) {
						System.out.println("I am here");
					}
					startIndex = endIndex;
				}
			} while (endIndex < (s.length()-1) && endIndex >= 0);
		}
	
		String msg=Arrays.toString(listm.toArray());
		System.out.println(msg);
		Files.write(Paths.get("./test.txt"), msg.getBytes());
		aDao.saveUserData(listm);
		System.out.println("The number of records processed : " + count);
		return count;
	}

	private static void handleSuppliments(PDDocument doc, int firstSupplementPage) throws IOException {
		System.out.println("in Here with the Document and with start Page : "+firstSupplementPage);
		int noOfPage=doc.getNumberOfPages();
		PDFTextStripper textStripper=new PDFTextStripper();
		int startIndexforAdditions;
		int endIndexforAdditions;
		int startIndexforDeletions;
		int endIndexforDeletions;
		int startIndexforCorrections;
		int i=firstSupplementPage;
		int endIndexforCorrections;		
		Map<String ,String> metaDataMap=new HashMap<String,String> ();
		List<Model> listm=new ArrayList<Model>();
		//for List of Additions
		do{
			textStripper.setStartPage(i);
			textStripper.setEndPage(i);
			String page=textStripper.getText(doc);
			if(i==firstSupplementPage){
				metaDataMap=extractSupplementMetaData(page);
			}

			startIndexforAdditions=page.indexOf("1 - List of Additions");
			endIndexforAdditions=page.indexOf("Additions",startIndexforAdditions+30);
			int startIndex = page.indexOf("Sex", startIndexforAdditions);
			int endIndex = 0;
			do {
				endIndex = page.indexOf("Sex", startIndex + 1);
				if(endIndex==-1){
					endIndex=page.length()-1;
				}
				if (endIndex >0) {
					String block = page.substring(startIndex, endIndex);
					Model m = new Model();
					try {
						m.setSectionNo(Integer.parseInt(metaDataMap.get(EdsConstants.METADATAMAP_KEY_SECTIONNO)));
						m.setSectionName(metaDataMap.get(EdsConstants.METADATAMAP_KEY_SECTIONNAME));
						SimpleDateFormat sdfmt1= new SimpleDateFormat("dd.MM.yyyy");
						if(metaDataMap.get(EdsConstants.METADATAMAP_KEY_DateOfQualification)!=null)
							m.setDateOfInception(sdfmt1.parse(metaDataMap.get(EdsConstants.METADATAMAP_KEY_DateOfQualification)));
						else
							m.setDateOfInception(sdfmt1.parse(metaDataMap.get(EdsConstants.METADATAMAP_KEY_DateOfPublication)));
						m.fillSupplementData(block);
					} catch (Exception e) {
						//
						e.printStackTrace();
					}
					listm.add(m);
					/*if (m.getSno() == 29) {
						System.out.println("I am here");
					}*/
					
					startIndex = endIndex;
				}
				if(endIndexforAdditions>=0){
					if(endIndex>=endIndexforAdditions)
						endIndex=-1;
				}
			} while (endIndex < (page.length()-1) && endIndex >= 0 );
			i++;
		}while(endIndexforAdditions==-1&& i<noOfPage);
		System.out.println((Arrays.toString(listm.toArray())));
		//aDao.saveUserData(listm);
		//For Deletions
		/*
		if(endIndexforAdditions!=0)i=endIndexforAdditions;
		do{
			textStripper.setStartPage(i);
			textStripper.setEndPage(i);
			String page=textStripper.getText(doc);
			startIndexforDeletions=page.indexOf("1 - List of Deletions");
			endIndexforDeletions=page.indexOf("Deletions",startIndexforDeletions+30);
			int startIndex = page.indexOf("Sex", startIndexforDeletions);
			int endIndex = 0;
			do {
				endIndex = page.indexOf("Sex", startIndex + 1);
				if(endIndex==-1){
					endIndex=page.length()-1;
				}
				if (endIndex >0) {
					String block = page.substring(startIndex, endIndex);
					Model m = new Model();
					try {
						handleDeletions(block);
					} catch (Exception e) {
						//
						e.printStackTrace();
					}
					listm.add(m);
					if (m.getSno() == 29) {
						System.out.println("I am here");
					}
					
					startIndex = endIndex;
				}
				if(endIndexforAdditions>=0){
					if(endIndex>=endIndexforAdditions)
						endIndex=-1;
				}
			} while (endIndex < (page.length()-1) && endIndex >= 0 );
			i++;		}while(endIndexforAdditions==-1&& i<noOfPage);

		//for Corrections
		if(endIndexforAdditions!=0)i=endIndexforAdditions;
		if(endIndexforDeletions!=0)i=endIndexforDeletions;
		do{			textStripper.setStartPage(i);
		textStripper.setEndPage(i);
		String page=textStripper.getText(doc);
		startIndexforAdditions=page.indexOf("1 - List of Additions");
		endIndexforAdditions=page.indexOf("Additions",startIndexforAdditions+30);
		int startIndex = page.indexOf("Sex", startIndexforAdditions);
		int endIndex = 0;
		do {
			endIndex = page.indexOf("Sex", startIndex + 1);
			if(endIndex==-1){
				endIndex=page.length()-1;
			}
			if (endIndex >0) {
				String block = page.substring(startIndex, endIndex);
				Model m = new Model();
				try {
					handleCorrections(block);
				} catch (Exception e) {
					//
					e.printStackTrace();
				}
				listm.add(m);
				if (m.getSno() == 29) {
					System.out.println("I am here");
				}
				
				startIndex = endIndex;
			}
			if(endIndexforAdditions>=0){
				if(endIndex>=endIndexforAdditions)
					endIndex=-1;
			}
		} while (endIndex < (page.length()-1) && endIndex >= 0 );
		i++;		}while(endIndexforAdditions==-1&& i<noOfPage);

		
		
		System.out.println("\n\nHandling Suppliments:\n\n"+"The List of Additions\n"+listm);
		
		
*/
		/*			PDFTextStripperByArea textStripperByArea=new PDFTextStripperByArea();
            int pageNum = 0;
              for( PDPage page : doc.getPages() )
            {
                pageNum++;
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                List annotations = page.getAnnotations();

                stripper.extractRegions( page );

                stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition( true );
                Rectangle rect = new Rectangle( 0, 0, 800, 120 );
                stripper.addRegion( "class1", rect );
                PDPage firstPage = doc.getPage(firstSupplementPage);
                stripper.extractRegions( firstPage );
                System.out.println( "Text in the area:" + rect );
                String strippedText=stripper.getTextForRegion( "class1" );
                System.out.println( stripper.getTextForRegion( "class1" ) );

            }
		 */		

	}

	public static void main(String[] args) throws IOException {
		process();
	}

	public static void mainToDownLoadInputDataFiles(String args[]) throws MalformedURLException, IOException {
		Integer AcNo = 65;
		Integer fileNo = 11;
		/*
		 * BufferedInputStream in=null; FileOutputStream fout=null;
		 */
		for (; AcNo < 71;) {
			String AcNoString = convert(AcNo, 2);
			String fileNoString = convert(fileNo, 4);
			String urlString = "http://ceodelhi.gov.in/WriteReadData/AssemblyConstituency4/AC" + (String) AcNoString
					+ "/AC0" + (String) AcNoString + fileNoString + ".pdf";
			System.out.println(urlString);
			URL website = new URL(urlString);
			String fileName = "AC0" + (String) AcNoString + fileNoString + ".pdf";
			try (InputStream in = website.openStream()) {
				Path p1 = Paths.get("E:\\EDS\\InputData\\" + fileName);
				Files.copy(in, p1);
			} catch (FileSystemAlreadyExistsException fae) {
				System.out.println("caught");
			} catch (Exception e) {
				int ch = 0;
				// if (!(e.getCause() == null)) {
				if (e.getCause().equals(new FileSystemAlreadyExistsException())) {
					ch++;
					// }
				}
				if (ch == 0) {
					System.out.println(e.getMessage());
					AcNo++;
					fileNo = 0;
				}
			}

			/*
			 * try { in = new BufferedInputStream(new
			 * URL(urlString).openStream()); fout = new
			 * FileOutputStream("E:\\EDS\\InputData\\");
			 * 
			 * final byte data[] = new byte[1024]; int count; while ((count =
			 * in.read(data, 0, 1024)) != -1) { fout.write(data, 0, count); } }
			 * finally { if (in != null) { in.close(); } if (fout != null) {
			 * fout.close(); } }
			 */
			finally {
				fileNo++;
				continue;
			}
		}

	}

	public static String convert(Integer num, int l) {
		String s = num.toString();
		while (s.length() < l) {
			s = "0" + s;
		}
		return s;
	}

	public static Map<String,String>  extractMetaData(String s) {
		Map<String, String> metaDateMap=new HashMap<>();
		try {
			String p1 = "(?s)(?<=Section No & Name).*?(?=\n)";//Section No & Name
			Pattern r1 = Pattern.compile(p1);
			Matcher m1 = r1.matcher(s);
			String s1=null;
			if (m1.find()) {
				s1 = m1.group(0);
				String sectionNoRegex="(?s)(?<=-).*?(?=-)";
				Pattern sectionNoPattern = Pattern.compile(sectionNoRegex);
				Matcher sectionNomatcher=sectionNoPattern.matcher(s1);
				if(sectionNomatcher.find()){
					metaDateMap.put(EdsConstants.METADATAMAP_KEY_SECTIONNO,sectionNomatcher.group(0).trim());
					s1.replaceFirst("- "+sectionNomatcher.group(0)+" -","");
				}
				metaDateMap.put(EdsConstants.METADATAMAP_KEY_SECTIONNAME, s1);
			}
			String pFooter="(?s)(?<=Age as on 01-01-2017 Page).*?(?=Issued By)";
			Pattern rFooter=Pattern.compile(pFooter);
			Matcher mFooter=rFooter.matcher(s);
			String sfooter=null;
			if(mFooter.find()){
				sfooter=mFooter.group(0);
				String [] pageNo=sfooter.split("of");
				metaDateMap.put("pageNo",pageNo[0].trim());
			}

			String pSupliment="(?s)(?<=Supplement Type).*?(?=1 - List)";
			Pattern rSuppliment=Pattern.compile(pSupliment);
			Matcher mSuppliment=rSuppliment.matcher(s);
			String sSuppliment=null;
			SimpleDateFormat sdfmt1 = new SimpleDateFormat("dd.MM.yy"); 
			if(mSuppliment.find()){
				sSuppliment=mSuppliment.group(0);
				String [] suplimentDetails=sSuppliment.split("\r\n");
				System.out.println(suplimentDetails);
				for(int jj=0;jj<suplimentDetails.length;jj++){
					String j=null;
					try{
						j=suplimentDetails[jj];
					}
					catch (Exception e) {
					}if(j!=null){
						String key=null;
						String value=null;
						String detail[]=j.split(":");
						java.util.Date qDate=null;
						try{
							qDate = sdfmt1.parse(j);
						}
						catch (Exception e) {
						}
						if(qDate!=null){
							metaDateMap.put(EdsConstants.METADATAMAP_KEY_DateOfQualification,qDate.toString());
						}
						if(detail!=null&&detail.length>=2){
							if(detail[0].trim().length()>0){
								key=detail[0].trim();
							}
							if(detail[1].trim().length()>0){
								value=detail[1].trim();
							}
							if(value!=null&&key!=null){
								if(key.length()>0&&value.length()>0){
									if(key.indexOf("Supplement No")>-1){
										key=EdsConstants.METADATAMAP_KEY_SUPPLEMENTNO;
									}
									if(key.indexOf("Mother Roll")>-1){
										key=EdsConstants.METADATAMAP_KEY_MOTHERROLL;
									}
									if(key.indexOf("Revision")>-1){
										key=EdsConstants.METADATAMAP_KEY_REVISIONID;
									}
									if(key.indexOf("Supplement Process & Year")>-1){
										key=EdsConstants.METADATAMAP_KEY_SUPPLEMENT_PROCESS_n_YEAR;
									}
									if(key.indexOf("Date of Publication")>-1){
										key=EdsConstants.METADATAMAP_KEY_DateOfPublication;
									}
									metaDateMap.put(key,value);
								}
							}
						}
					}
				}
				//metaDateMap.put("pageNo",pageNo[0].trim());
			}


		} catch (Exception e) {
			System.out.println("wec");
		}
		return metaDateMap;

	}
	
	public static Map<String,String>  extractSupplementMetaData(String s) {
		Map<String, String> metaDateMap=new HashMap<>();
		try {
			String p1 = "(?s)(?<=Section No\\. & Name).*?(?=\n)";//Section No & Name
			Pattern r1 = Pattern.compile(p1);
			Matcher m1 = r1.matcher(s);
			String s1=null;
			if (m1.find()) {
				s1 = m1.group(0);
				String sectionNoRegex="(?s)(?<=-).*?(?=-)";
				Pattern sectionNoPattern = Pattern.compile(sectionNoRegex);
				Matcher sectionNomatcher=sectionNoPattern.matcher(s1);
				if(sectionNomatcher.find()){
					metaDateMap.put(EdsConstants.METADATAMAP_KEY_SECTIONNO,sectionNomatcher.group(0).trim());
					s1.replaceFirst("- "+sectionNomatcher.group(0)+" -","");
				}
				metaDateMap.put(EdsConstants.METADATAMAP_KEY_SECTIONNAME, s1);
			}
			String pFooter="(?s)(?<=Age as on 01-01-2017 Page).*?(?=Issued By)";
			Pattern rFooter=Pattern.compile(pFooter);
			Matcher mFooter=rFooter.matcher(s);
			String sfooter=null;
			if(mFooter.find()){
				sfooter=mFooter.group(0);
				String [] pageNo=sfooter.split("of");
				metaDateMap.put("pageNo",pageNo[0].trim());
			}

			String pSupliment="(?s)(?<=Supplement Type).*?(?=1 - List)";
			Pattern rSuppliment=Pattern.compile(pSupliment);
			Matcher mSuppliment=rSuppliment.matcher(s);
			String sSuppliment=null;
			SimpleDateFormat sdfmt1 = new SimpleDateFormat("dd.MM.yy"); 
			if(mSuppliment.find()){
				sSuppliment=mSuppliment.group(0);
				String [] suplimentDetails=sSuppliment.split("\r\n");
				System.out.println(suplimentDetails);
				for(int jj=0;jj<suplimentDetails.length;jj++){
					String j=null;
					try{
						j=suplimentDetails[jj];
					}
					catch (Exception e) {
					}if(j!=null){
						String key=null;
						String value=null;
						String detail[]=j.split(":");
						java.util.Date qDate=null;
						try{
							qDate = sdfmt1.parse(j);
						}
						catch (Exception e) {
						}
						if(qDate!=null){
							metaDateMap.put(EdsConstants.METADATAMAP_KEY_DateOfQualification,j.trim());
						}
						if(detail!=null&&detail.length>=2){
							if(detail[0].trim().length()>0){
								key=detail[0].trim();
							}
							if(detail[1].trim().length()>0){
								value=detail[1].trim();
							}
							if(value!=null&&key!=null){
								if(key.length()>0&&value.length()>0){
									if(key.indexOf("Supplement No")>-1){
										key=EdsConstants.METADATAMAP_KEY_SUPPLEMENTNO;
									}
									if(key.indexOf("Mother Roll")>-1){
										key=EdsConstants.METADATAMAP_KEY_MOTHERROLL;
									}
									if(key.indexOf("Revision")>-1){
										key=EdsConstants.METADATAMAP_KEY_REVISIONID;
									}
									if(key.indexOf("Supplement Process & Year")>-1){
										key=EdsConstants.METADATAMAP_KEY_SUPPLEMENT_PROCESS_n_YEAR;
									}
									if(key.indexOf("Date of Publication")>-1){
										key=EdsConstants.METADATAMAP_KEY_DateOfPublication;
									}
									metaDateMap.put(key,value);
								}
							}
						}
					}
				}
				//metaDateMap.put("pageNo",pageNo[0].trim());
			}


		} catch (Exception e) {
			System.out.println("wec");
		}
		return metaDateMap;

	}
	
	private static void handleCorrections(String block) {
		// TODO Auto-generated method stub
		
	}

	private static void handleDeletions(String block) {
		// TODO Auto-generated method stub
		
	}

}
