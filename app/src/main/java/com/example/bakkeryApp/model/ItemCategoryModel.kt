package com.cbe.inventory.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ItemCategoryModel {
	@SerializedName("id")
	@Expose
	var id: Long? = null

	@SerializedName("createdBy")
	@Expose
	var createdBy: Any? = null

	@SerializedName("createdOn")
	@Expose
	var createdOn: String? = null

	@SerializedName("modifiedOn")
	@Expose
	var modifiedOn: String? = null

	@SerializedName("modifiedBy")
	@Expose
	var modifiedBy: Any? = null

	@SerializedName("name")
	@Expose
	val name: String? = null
}