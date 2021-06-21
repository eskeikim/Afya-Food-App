package com.apps.skimani.afyafood.models


import com.google.gson.annotations.SerializedName

data class InstantFoodItemResponse(
    @SerializedName("branded")
    val branded: List<BrandedList>,
    @SerializedName("common")
    val common: List<CommonList>
)
    data class BrandedList(
        @SerializedName("brand_name")
        val brandName: String, // Taco Villa
        @SerializedName("brand_name_item_name")
        val brandNameItemName: String, // Taco Villa Southwest Omlet - Bacon
        @SerializedName("brand_type")
        val brandType: Int, // 1
        @SerializedName("food_name")
        val foodName: String, // Southwest Omlet - Bacon
        @SerializedName("locale")
        val locale: String, // en_US
        @SerializedName("nf_calories")
        val nfCalories: Any, // 440
        @SerializedName("nix_brand_id")
        val nixBrandId: String, // 59e901517bcefb95678b5998
        @SerializedName("nix_item_id")
        val nixItemId: String, // c64062546e122579182b2738
        @SerializedName("photo")
        val photo: BrandedPhoto,
        @SerializedName("region")
        val region: Int, // 1
        @SerializedName("serving_qty")
        val servingQty: Any, // 213
        @SerializedName("serving_unit")
        val servingUnit: String // grams
    )
        data class BrandedPhoto(
            @SerializedName("highres")
            val highres: Any, // null
            @SerializedName("is_user_uploaded")
            val isUserUploaded: Boolean, // false
            @SerializedName("thumb")
            val thumb: String // https://d2eawub7utcl6.cloudfront.net/images/nix-apple-grey.png
        )


    data class CommonList(
        @SerializedName("common_type")
        val commonType: Any, // null
        @SerializedName("food_name")
        val foodName: String, // cheese omlette
        @SerializedName("locale")
        val locale: String, // en_US
        @SerializedName("photo")
        val photo: CommonPhoto,
        @SerializedName("serving_qty")
        val servingQty: Any, // 1
        @SerializedName("serving_unit")
        val servingUnit: String, // 3-egg omelet
        @SerializedName("tag_id")
        val tagId: String, // 3917
        @SerializedName("tag_name")
        val tagName: String // cheese omelet
    )
        data class CommonPhoto(
            @SerializedName("thumb")
            val thumb: String // https://nix-tag-images.s3.amazonaws.com/3917_thumb.jpg
        )

