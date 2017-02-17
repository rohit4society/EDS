package com.tihor.ocr.dao;

import java.lang.reflect.Field;
import java.util.List;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoDriver;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.tihor.ocr.domain.Model;

public class ArangoDao implements EdsDao {
	final String collectionName="voterData";
	static ArangoDriver arangoDriver;
	static {
		init();
	}
	private static void init(){
		ArangoConfigure configure = new ArangoConfigure("/properties/ArrangoDB.properties");
		configure.init();
		arangoDriver = new ArangoDriver(configure);
	}
	
	public static void main(String[] args) {
		ArangoConfigure configure = new ArangoConfigure("/properties/ArrangoDB.properties");
		configure.init();
		ArangoDriver arangoDriver = new ArangoDriver(configure);
		String collectionName = "userdata_electoralRoll";
		try {
		  CollectionEntity myArangoCollection = arangoDriver.createCollection(collectionName);
		  System.out.println("Collection created: " + myArangoCollection.getName());
		} catch (Exception e) { 
		  System.out.println("Failed to create colleciton " + collectionName + "; " + e.getMessage()); 
		}
		
		/*BaseDocument userData=new BaseDocument();
		userData.setDocumentKey("testElectoralRollNumber");
		userData.addAttribute("name","ramu");
		try{
			ArangoDriver
		}*/
		
}  


	public void saveUserData(List<Model> list) {
		for(Model user:list){		
		try {
			  arangoDriver.createDocument("userdata_electoralRoll", userDataToDBUserData(user));
			  System.out.println("Document created"); 
			} catch (Exception e) {  
			  System.out.println("Failed to create document. " + e.getMessage()); 
			}
		}
	}

	public List<Model> getUserData() {
		
		return null;
	}

	public void updateUserData(Model updatedUser) {
		// TODO Auto-generated method stub
		
	}
		
		
	public static BaseDocument userDataToDBUserData(Model userData){
		BaseDocument dbUserData=new BaseDocument();
		Field[] fields=userData.getClass().getDeclaredFields();
		
		dbUserData.setDocumentKey(handleSlash(userData.getRollNo()));
		for(Field f:fields){
			try {
				dbUserData.addAttribute(f.getName(),f.get(userData));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return dbUserData;
	}
	public static String handleSlash(String s){
		if(s.contains("/")||s.contains("\\")){
			s.replaceAll("/", "-");
		}
		return s;
	}
}
