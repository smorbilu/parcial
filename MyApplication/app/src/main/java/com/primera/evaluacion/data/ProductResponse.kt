package com.primera.evaluacion.data

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val id: String,
    val name: String,
    val data: ProductData?
)

data class ProductData(
    val color: String?,
    val capacity: String?,
    val price: Double?,
    @SerializedName("capacity GB")
    val capacityGB: Int?,
    val generation: String?,
    val year: Int?,
    @SerializedName("CPU model")
    val cpuModel: String?,
    @SerializedName("Hard disk size")
    val hardDiskSize: String?,
    @SerializedName("Strap Colour")
    val strapColour: String?,
    @SerializedName("Case Size")
    val caseSize: String?,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("Screen size")
    val screenSize: Double?,
    @SerializedName("Capacity")
    val capacityAlt: String?,
    @SerializedName("Generation")
    val generationAlt: String?,
    @SerializedName("Price")
    val priceAlt: String?,
    @SerializedName("Color")
    val colorAlt: String?
)
