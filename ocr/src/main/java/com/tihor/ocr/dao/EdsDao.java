package com.tihor.ocr.dao;

import java.util.List;

import com.tihor.ocr.domain.Model;

public interface EdsDao {
public void saveUserData(List<Model> list);
public List<Model> getUserData();
public void updateUserData(Model updatedUser);
}
