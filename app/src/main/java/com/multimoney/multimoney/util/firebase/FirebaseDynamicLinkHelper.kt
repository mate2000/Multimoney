package com.multimoney.multimoney.util.firebase

class FirebaseDynamicLinkHelper {
    // TODO: Dynamic Link example
//    val shortLink = MutableLiveData<String>()
//
//    fun getDynamicLink(
//        bigCommerceProductId: Long?,
//        imageUrl: String?,
//        name: String
//    ) {
//        Firebase.dynamicLinks.shortLinkAsync {
//            longLink =
//                Uri.parse(
//                    "$DYNAMIC_LINK_PREFIX?link=" +
//                            "$SITE_URL/$PATH_PRODUCT?$ID_PARAMETER=" +
//                            "$bigCommerceProductId&apn=" +
//                            "$ANDROID_BUNDLE_ID&ibi=" +
//                            "$IOS_BUNDLE_ID&ofl=" +
//                            "$SITE_URL&isi=" +
//                            "$APP_STORE_ID&si=" +
//                            "$imageUrl&st=" + name
//                )
//        }.addOnSuccessListener { result ->
//            // Short link created
//            val shortLink = result.shortLink
//            this.shortLink.value = shortLink.toString()
//        }.addOnFailureListener {
//            // Error
//            this.shortLink.postValue(it.localizedMessage)
//        }
//    }
//
//    companion object {
//        const val ID_PARAMETER = "id"
//        const val PATH_PRODUCT = "Product"
//        const val PATH_REFERRED = "Referido"
//    }
}