package com.example.bakkerykotlin.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemsModel {

    @SerializedName("createdBy")
    @Expose
    private Object createdBy;
    @SerializedName("createdOn")
    @Expose
    private String createdOn;
    @SerializedName("updatedOn")
    @Expose
    private Object updatedOn;
    @SerializedName("updatedBy")
    @Expose
    private Object updatedBy;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("nameInTamil")
    @Expose
    private Object nameInTamil;
    @SerializedName("itemCategory")
    @Expose
    private String itemCategory;
    @SerializedName("costPrice")
    @Expose
    private Integer costPrice;
    @SerializedName("sellingPrice")
    @Expose
    private Integer sellingPrice;
    @SerializedName("taxPercentage")
    @Expose
    private Integer taxPercentage;
    @SerializedName("taxIncluded")
    @Expose
    private Boolean taxIncluded;
    @SerializedName("unitOfMeasure")
    @Expose
    private String unitOfMeasure;
    @SerializedName("imageFileName")
    @Expose
    private String imageFileName;
    @SerializedName("itemHistory")
    @Expose
    private List<ItemHistory> itemHistory = null;
    @SerializedName("imagePath")
    @Expose
    private Object imagePath;

    public Object getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Object createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Object getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Object updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getNameInTamil() {
        return nameInTamil;
    }

    public void setNameInTamil(Object nameInTamil) {
        this.nameInTamil = nameInTamil;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public Integer getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Integer costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Integer sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Integer taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public Boolean getTaxIncluded() {
        return taxIncluded;
    }

    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public List<ItemHistory> getItemHistory() {
        return itemHistory;
    }

    public void setItemHistory(List<ItemHistory> itemHistory) {
        this.itemHistory = itemHistory;
    }

    public Object getImagePath() {
        return imagePath;
    }

    public void setImagePath(Object imagePath) {
        this.imagePath = imagePath;
    }

    public class ItemHistory {

        @SerializedName("name")
        @Expose
        private Object name;
        @SerializedName("nameInTamil")
        @Expose
        private Object nameInTamil;
        @SerializedName("itemCategory")
        @Expose
        private String itemCategory;
        @SerializedName("costPrice")
        @Expose
        private Integer costPrice;
        @SerializedName("sellingPrice")
        @Expose
        private Integer sellingPrice;
        @SerializedName("taxPercentage")
        @Expose
        private Integer taxPercentage;
        @SerializedName("taxIncluded")
        @Expose
        private Boolean taxIncluded;
        @SerializedName("unitOfMeasure")
        @Expose
        private String unitOfMeasure;
        @SerializedName("createdOn")
        @Expose
        private String createdOn;

        public Object getName() {
            return name;
        }

        public void setName(Object name) {
            this.name = name;
        }

        public Object getNameInTamil() {
            return nameInTamil;
        }

        public void setNameInTamil(Object nameInTamil) {
            this.nameInTamil = nameInTamil;
        }

        public String getItemCategory() {
            return itemCategory;
        }

        public void setItemCategory(String itemCategory) {
            this.itemCategory = itemCategory;
        }

        public Integer getCostPrice() {
            return costPrice;
        }

        public void setCostPrice(Integer costPrice) {
            this.costPrice = costPrice;
        }

        public Integer getSellingPrice() {
            return sellingPrice;
        }

        public void setSellingPrice(Integer sellingPrice) {
            this.sellingPrice = sellingPrice;
        }

        public Integer getTaxPercentage() {
            return taxPercentage;
        }

        public void setTaxPercentage(Integer taxPercentage) {
            this.taxPercentage = taxPercentage;
        }

        public Boolean getTaxIncluded() {
            return taxIncluded;
        }

        public void setTaxIncluded(Boolean taxIncluded) {
            this.taxIncluded = taxIncluded;
        }

        public String getUnitOfMeasure() {
            return unitOfMeasure;
        }

        public void setUnitOfMeasure(String unitOfMeasure) {
            this.unitOfMeasure = unitOfMeasure;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

    }
}