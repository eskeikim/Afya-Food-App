package com.apps.skimani.afyafood.utils

class Constants {
    companion object  {
        /**
         * Base URL for the nutrix api endpoin
         */
        var BASE_URL="https://trackapi.nutritionix.com/v2/"

        /**
         * Pinner Cert sha 256 to help in certificate pinning
         */
        var PINNER_CERT="sha256/fajdlzqjFkH3fU8/NrjW0d4cFANUzh/4HstyvlVaTqM="

        /**
         * Pinner Cert URL to help in certificate pinning
         */
        var PINNER_URL="trackapi.nutritionix.com"

        /**
         * Nutritionix Api App ID- replace with Yours
         */
        var APP_ID="b9860860"
        /**
         * Nutritionix Api App KEY- replace with Yours
         */
        var APP_KEY="cd2f1b032944923808854e03cdf48b66"
    }


}