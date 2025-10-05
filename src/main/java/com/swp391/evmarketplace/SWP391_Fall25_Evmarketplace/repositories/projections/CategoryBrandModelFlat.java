package com.swp391.evmarketplace.SWP391_Fall25_Evmarketplace.repositories.projections;

public interface CategoryBrandModelFlat {
    Long   getCategoryId();
    String getCategoryName();
    String getCategoryDescription();
    String getCategoryStatus();

    Long   getBrandId();
    String getBrandName();
    String getBrandStatus();

    Long    getModelId();      // có thể null
    String  getModelName();    // có thể null
    Integer getModelYear();    // có thể null
    String  getModelStatus();
}
