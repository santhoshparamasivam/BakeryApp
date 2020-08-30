package com.cbe.bakery.model.payload

class StockByItemResponse {

    var itemId: Long? = null

    var itemName: String? = null

    var stock: List<StockByItemDtlResponse>? = null


}