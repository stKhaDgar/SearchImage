package com.example.stmak.searchimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.stmak.searchimage.model.ImageBD
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        val items = ArrayList<ImageBD>()
        val allImages = realm.where(ImageBD::class.java).sort("date", Sort.DESCENDING).findAll()
        allImages.forEach { image ->
            items.add(image)
        }

        grid_images_history.adapter = GridHistoryAdapter(this@HistoryActivity, items)

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
