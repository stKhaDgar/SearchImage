package com.example.stmak.searchimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.stmak.searchimage.model.ImageBD
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        Realm.init(this)
        val realm = Realm.getDefaultInstance()

        val allImages = realm.where(ImageBD::class.java).findAll()
        allImages.forEach { image ->
            println("ImageID : ${image.id}")
        }

    }
}
