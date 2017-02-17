package com.tihor.ocr.domain;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
@NodeEntity
public class Model {

	public Integer sno;
	public String sex;
	@GraphId
	public String rollNo;
	public String fathersName;
	public String husbandsName;
	public Integer age;
	public String houseNo;
	public String careOf;
	public String mothersName;
	public String othersName;
	public String name;
	public List<String> address;
	public Integer SectionNo;
	public String sectionName;
	public Date dateOfInception;
	public String status="a";//a active,s shifted,
	public Integer getSectionNo() {
		return SectionNo;
	}

	public Date getDateOfInception() {
		return dateOfInception;
	}

	public void setDateOfInception(Date dateOfInception) {
		this.dateOfInception = dateOfInception;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		status = status;
	}

	public void setSectionNo(Integer sectionNo) {
		SectionNo = sectionNo;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public String getHusbandsName() {
		return husbandsName;
	}

	public void setHusbandsName(String husbandsName) {
		this.husbandsName = husbandsName;
	}
	public String getMothersName() {
		return mothersName;
	}

	public void setMothersName(String mothersName) {
		this.mothersName = mothersName;
	}

	public String getOthersName() {
		return othersName;
	}

	public void setOthersName(String othersName) {
		this.othersName = othersName;
	}


	@Override
	public String toString() {
		/*return ("S.no : " + this.sno + "\tName : " + this.name + "\tElectoralRoll : " + this.rollNo + "\tCareOf : "
				+ this.careOf + "\tFathers Name : " + this.fathersName + "\tHusbands Name : " + this.husbandsName
				+ "\tSex : " + this.sex + "\tHouseNo. : " + this.houseNo + "\tAge : " + this.age+"\tSectionNo : " + this.SectionNo+"\tSectionName : " + this.sectionName);
	*/
		return ("S.no : ," + this.sno + ",Name : ," + this.name + ",ElectoralRoll : ," + this.rollNo + ",CareOf : ,"
				+ this.careOf + ",Fathers Name : ," + this.fathersName + ",Husbands Name : ," + this.husbandsName
				+ ",Sex : ," + this.sex + ",HouseNo. : ," + this.houseNo + ",Age : ," + this.age+",SectionNo : ," + this.SectionNo+",SectionName : ," + this.sectionName);
	
	}

	public void fill(String block) throws Exception {
		try {
			String p1 = "(?s)(?<=Sex).*?(?=ale)";
			Pattern r1 = Pattern.compile(p1);
			Matcher m1 = r1.matcher(block);
			if (m1.find()) {
				String s1 = m1.group(0);
				this.sex = s1.substring(3, 4);
			}
			String p2 = "(?s)(?<=Age : ).*?(?=H)";
			Pattern r2 = Pattern.compile(p2);
			Matcher m2 = r2.matcher(block);
			if (m2.find()) {
				String s2 = m2.group(0);
				this.age = Integer.parseInt(s2.trim());
			}

			String p3 = "(?s)(?<=Name :).*?(?=Name)";
			Pattern r3 = Pattern.compile(p3);
			Matcher m3 = r3.matcher(block);
			if (m3.find()) {
				String s3 = m3.group(0);
				String str[] = s3.split("\r\n");
				// System.out.println(str[0]+" "+str[1]+" "+str[2]);
				this.name = str[2].trim();
				String str2[] = str[1].split(" ");
				this.sno = Integer.parseInt(str2[1].trim());
				if(this.sno==null){
					System.out.println("ssjd");
				}
				this.rollNo = str2[2].trim();
				if (str[str.length-1].trim().equalsIgnoreCase("Father's")) {
					this.setCareOf("father");
				}
				if (str[3].trim().equalsIgnoreCase("Husband's")) {
					this.setCareOf("husband");
				}
				if (str[3].trim().equalsIgnoreCase("Other's")) {
					this.setCareOf("husband");
				}
				if (str[3].trim().equalsIgnoreCase("Mother's")) {
					this.setCareOf("husband");
				}
				if(this.careOf==null)
				{throw new Exception();}

			}
			String p4 = "(?s)(?<=Father's|Husband's).*?(?=:)";
			Pattern r4 = Pattern.compile(p4);
			Matcher m4 = r4.matcher(block);
			if (m4.find()) {
				String s4 = m4.group(0);
				String str3[] = s4.split("\r\n");
				String str2[] = str3[1].split(" ");
				String fhName = null;
				if (str2[0].equalsIgnoreCase("name")) {
					fhName = tillEnd(str2, 1);
				}
				if (this.getCareOf().equalsIgnoreCase("father"))
					this.fathersName = fhName;
				if (this.getCareOf().equalsIgnoreCase("husband"))
					this.husbandsName = fhName;
				if (this.getCareOf().equalsIgnoreCase("mother"))
					this.mothersName = fhName;
				if (this.getCareOf().equalsIgnoreCase("other"))
					this.othersName = fhName;
				this.houseNo = str3[str3.length - 1].trim();
			
			
			if(this.getAge()==null)
				throw new Exception("Age not set");
			if(this.getCareOf()==null)
				throw new Exception("C/o not set");
			if(this.getFathersName()==null && this.getHusbandsName()==null&&this.getMothersName()==null&&this.getOthersName()==null)
				throw new Exception("C/o Name not set");
			if(this.getHouseNo()==null)
				throw new Exception("House no not set");
			if(this.getName()==null)
				throw new Exception("Name not set");
			if(this.getRollNo()==null)
				throw new Exception("Rollno not set");
			if(this.getSno()==null)
				throw new Exception("Sno not set");
			}
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			throw e;
		}
	}

	public void fillSupplementData(String block) throws Exception {
		try {
			String p1 = "(?s)(?<=Sex).*?(?=ale)";
			Pattern r1 = Pattern.compile(p1);
			Matcher m1 = r1.matcher(block);
			if (m1.find()) {
				String s1 = m1.group(0);
				this.sex = s1.substring(3, 4);
			}
			String p2 = "(?s)(?<=Age : ).*?(?=H)";
			Pattern r2 = Pattern.compile(p2);
			Matcher m2 = r2.matcher(block);
			if (m2.find()) {
				String s2 = m2.group(0);
				this.age = Integer.parseInt(s2.trim());
			}

			String p3 = "(?s)(?<=Name    :).*?(?=Name)";
			Pattern r3 = Pattern.compile(p3);
			Matcher m3 = r3.matcher(block);
			if (m3.find()) {
				String s3 = m3.group(0);
				String str[] = s3.split("\r\n");
				// System.out.println(str[0]+" "+str[1]+" "+str[2]);
				this.name = str[3].trim();
				//String str2[] = str[1].split(" ");
				this.sno = Integer.parseInt(str[2].trim());
				if(this.sno==null){
					System.out.println("ssjd");
				}
				//this.rollNo = str2[2].trim();
				if (str[str.length-1].trim().equalsIgnoreCase("Father's")) {
					this.setCareOf("father");
				}
				if (str[str.length-1].trim().equalsIgnoreCase("Husband's")) {
					this.setCareOf("husband");
				}
				if (str[str.length-1].trim().equalsIgnoreCase("Other's")) {
					this.setCareOf("husband");
				}
				if (str[str.length-1].trim().equalsIgnoreCase("Mother's")) {
					this.setCareOf("husband");
				}
				if(this.careOf==null)
				{throw new Exception();}  

			}
			String str2[]=block.split("\r\n");
			
			this.rollNo=str2[str2.length-1];
			this.houseNo=str2[1].substring(str2[1].indexOf("House No :")+11);
			String fhName=str2[str2.length-4];
			if (this.getCareOf().equalsIgnoreCase("father"))
				this.fathersName = fhName;
			if (this.getCareOf().equalsIgnoreCase("husband"))
				this.husbandsName = fhName;
			if (this.getCareOf().equalsIgnoreCase("mother"))
				this.mothersName = fhName;
			if (this.getCareOf().equalsIgnoreCase("other"))
				this.othersName = fhName;
		/*	String p4 = "(?s)(?<=Father's|Husband's).*?(?=:)";
			Pattern r4 = Pattern.compile(p4);
			Matcher m4 = r4.matcher(block);
			if (m4.find()) {
				String s4 = m4.group(0);
				String str3[] = s4.split("\r\n");
				String str2[] = str3[1].split(" ");
				String fhName = null;
				if (str2[0].equalsIgnoreCase("name")) {
					fhName = tillEnd(str2, 1);
				}
				if (this.getCareOf().equalsIgnoreCase("father"))
					this.fathersName = fhName;
				if (this.getCareOf().equalsIgnoreCase("husband"))
					this.husbandsName = fhName;
				if (this.getCareOf().equalsIgnoreCase("mother"))
					this.mothersName = fhName;
				if (this.getCareOf().equalsIgnoreCase("other"))
					this.othersName = fhName;
				this.houseNo = str3[str3.length - 1].trim();
			*/
			
			if(this.getAge()==null)
				throw new Exception("Age not set");
			if(this.getCareOf()==null)
				throw new Exception("C/o not set");
			if(this.getFathersName()==null && this.getHusbandsName()==null&&this.getMothersName()==null&&this.getOthersName()==null)
				throw new Exception("C/o Name not set");
			if(this.getHouseNo()==null)
				throw new Exception("House no not set");
			if(this.getName()==null)
				throw new Exception("Name not set");
			if(this.getRollNo()==null)
				throw new Exception("Rollno not set");
			if(this.getSno()==null)
				throw new Exception("Sno not set");
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public void setFname(String fname) {
		this.fathersName = fname;
	}

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getCareOf() {
		return careOf;
	}

	public void setCareOf(String careOf) {
		this.careOf = careOf;
	}

	public static String tillEnd(String[] str, int startIndex) {
		String s = "";
		for (int i = startIndex; i < str.length; i++) {
			s = s + str[i];
		}
		return s;
	}

	public List<String> getAddress() {
		return address;
	}

	public void setAddress(List<String> address) {
		this.address = address;
	}
}