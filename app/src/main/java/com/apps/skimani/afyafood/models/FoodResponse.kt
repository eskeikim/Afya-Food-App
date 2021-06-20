package com.apps.skimani.afyafood.models


import com.google.gson.annotations.SerializedName

 data class FoodResponse (
    @SerializedName("foods")
    val foods: List<Food>
)
     data class Food(
        @SerializedName("consumed_at")
        val consumedAt: String, // 2021-06-14T12:00:00+00:00
        @SerializedName("food_name")
        val foodName: String, // eggs
        @SerializedName("meal_type")
        val mealType: Int, // 1
        @SerializedName("ndb_no")
        val ndbNo: Double, // 1123
        @SerializedName("nf_calories")
        val nfCalories: Double, // 143
        @SerializedName("nf_cholesterol")
        val nfCholesterol: Double, // 372
        @SerializedName("nf_dietary_fiber")
        val nfDietaryFiber: Double, // 0
        @SerializedName("nf_potassium")
        val nfPotassium: Double, // 138
        @SerializedName("nf_protein")
        val nfProtein: Double, // 12.56
        @SerializedName("nf_saturated_fat")
        val nfSaturatedFat: Double, // 3.13
        @SerializedName("nf_sodium")
        val nfSodium: Double, // 142
        @SerializedName("nf_sugars")
        val nfSugars: Double, // 0.37
        @SerializedName("nf_total_carbohydrate")
        val nfTotalCarbohydrate: Double, // 0.72
        @SerializedName("nf_total_fat")
        val nfTotalFat: Double, // 9.51
        @SerializedName("photo")
        val photo: Photo,
        @SerializedName("serving_qty")
        val servingQty: Int, // 2
        @SerializedName("serving_unit")
        val servingUnit: String, // large
        @SerializedName("serving_weight_grams")
        val servingWeightGrams: Double // 100
    )

        data class Photo(
            @SerializedName("highres")
            val highres: String, // https://nix-tag-images.s3.amazonaws.com/542_highres.jpg
            @SerializedName("is_user_uploaded")
            val isUserUploaded: Boolean, // false
            @SerializedName("thumb")
            val thumb: String // https://nix-tag-images.s3.amazonaws.com/542_thumb.jpg
        )

