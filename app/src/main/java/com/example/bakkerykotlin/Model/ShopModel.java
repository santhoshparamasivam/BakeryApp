package com.example.bakkerykotlin.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShopModel {

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
@SerializedName("location")
@Expose
private String location;
@SerializedName("email")
@Expose
private Object email;
@SerializedName("mobile")
@Expose
private Object mobile;

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

public String getLocation() {
return location;
}

public void setLocation(String location) {
this.location = location;
}

public Object getEmail() {
return email;
}

public void setEmail(Object email) {
this.email = email;
}

public Object getMobile() {
return mobile;
}

public void setMobile(Object mobile) {
this.mobile = mobile;
}

}