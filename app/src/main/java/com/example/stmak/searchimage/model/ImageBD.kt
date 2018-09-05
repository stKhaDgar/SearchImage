package com.example.stmak.searchimage.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class ImageBD : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var date: String? = null
    var thumbUrl: String? = null
    var smallUrl: String? = null
    var word: String? = null
}