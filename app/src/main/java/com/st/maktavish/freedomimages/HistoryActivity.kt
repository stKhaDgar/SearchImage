package com.st.maktavish.freedomimages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.st.maktavish.freedomimages.model.ImageBD
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_history)

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        // В этом участке кода мы просто считываем все находящиеся в базе данных первые изображения результатов поиска
        // и записываем их в массив, который далее используем в адаптере сетки
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
