package com.example.stmak.searchimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.TextView
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.stmak.searchimage.model.ImageBD
import io.realm.Realm
import java.util.*

class MainActivity : AppCompatActivity() {
    // Массив для наших картинок различного размера (маленькие и средние)
    val items = ArrayList<Image>()

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidNetworking.initialize(applicationContext)

        onClickListener()

        Realm.init(this)
        realm = Realm.getDefaultInstance()

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    // Вынес обработчики событий по нажатию в отдельную функцию для компактности и наглядности кода
    private fun onClickListener() {
        // По нажатию на кнопку ПОИСК выполняется get-запрос, в котором мы получаем наши картинки
        button_search.setOnClickListener {
            use_search_tw.visibility = View.INVISIBLE
            getImages(et_search.text.toString())
        }

        // "Сворачиваем" нашу большую картинку и таким образом возвращаемся на поиск
        // P.S. "блокировка" приложения позади увеличинной картинки происходит за счёт обычного отключения
        // нашего grid_images. Не совсем верный подход, но для данной ситуации вполне подходит такой метод
        // в связи с упрощением кода и избежанием не нужной ресурсозатратности, как если бы я сделал это иначе
        img_big.setOnClickListener {
            grid_images_history.isEnabled = true
            img_big.visibility = View.INVISIBLE
        }

        // Обработчик событий по нажатию клавиши Готово на клавиатуре, после ввода слова в поле
        et_search.setOnEditorActionListener( TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                use_search_tw.visibility = View.INVISIBLE
                getImages(et_search.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        // Переходим на страницу истории поиска
        button_history.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Увеличение картинки по тапу. Так же стоит заметить, что из нашего массива мы достаем вариант картинки с
        // лучшим качеством.
        // P.S. Для подгрузки изображений из интернета я воспользовался библиотекой
        // Picasso, так как она довольно проста в использовании, а данное задание не требует чего-то, с чем
        // бы она не справилась
        grid_images_history.setOnItemClickListener { _, _, position, _ ->
            img_big.visibility = View.VISIBLE
            Picasso.with(this).load(items[position].smallUrl).into(img_big)
            grid_images_history.isEnabled = false
        }
    }

    // Та самая функция получения картинок при помощи get-запроса.
    private fun getImages(word: String) {
        loader_animation.visibility = View.VISIBLE
        loader_animation.playAnimation()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        items.clear()
        grid_images_history.adapter = null
        AndroidNetworking.get("https://api.unsplash.com/search/photos")
                .addQueryParameter("client_id", "dd867c1e011d5e088cba40b30db92299c48256a424fba6fa19fa931388bc0817")
                .addQueryParameter("page", "1")
                .addQueryParameter("per_page", "20")
                .addQueryParameter("query", word)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        loader_animation.visibility = View.INVISIBLE

                        // Если нам приходит пустой json без картинок (к примеру, на неккоректное слово в поиске),
                        // то на экране появляется соответствующая надпись, а дальнейшее выполнение кода прерывается
                        // командой 'return'
                        if(response.getString("total") == "0") {
                            val str = "К сожалению нам не удалось ничего найти на запрос: \"$word\""
                            use_search_tw.text = str
                            use_search_tw.visibility = View.VISIBLE
                            return
                        }

                        // В данном месте я решил сначала вытянуть необходимые мне данные (большой и малый размер картинок) в
                        // тот самый массив картинок, ссылкой на который является глобальная переменная items. В таком случае
                        // в адаптер нам не придется передавать весь JSONObject и там его обрабатывать, вытягивая нужные данные.
                        // Мы просто передаем в адаптер то, что нам нужно, а там в зависимости от позиции элемента в grid-е
                        // подставляем соответсвующую картинку из массива. Такой способ мне показался более эффективным
                        for(i in 0..19) {
                            items.add(items.size, Image(response.getJSONArray("results").getJSONObject(i).getJSONObject("urls").getString("thumb"),
                                    response.getJSONArray("results").getJSONObject(i).getJSONObject("urls").getString("small")))

                        }

                        // Как было указано в тех задании, в базу данных необходимо записать текст поиска и первое изображение
                        // результата поиска, что и происходит при помощи данного участка кода
                        writeToBD(Image(response.getJSONArray("results").getJSONObject(0).getJSONObject("urls").getString("thumb"),
                                response.getJSONArray("results").getJSONObject(0).getJSONObject("urls").getString("small")),
                                response.getJSONArray("results").getJSONObject(0).getString("id"),
                                System.currentTimeMillis().toString())

                        grid_images_history.adapter = GridMainAdapter(this@MainActivity, items)
                    }

                    override fun onError(error: ANError) {
                        Log.e("ErrorTask", error.errorDetail)
                    }
                })
    }

    // Функция записи необходимых данных в БД
    private fun writeToBD(image: Image, id: String, date: String){

        // Данная переменная служит указателем на то, есть ли в нашей базе уже подобный запрос...
        val item = realm.where(ImageBD::class.java).equalTo("word", et_search.text.toString()).findFirst()

        // ...если нет (null), то мы соответственно производим вставку полученных данных в базу...
        if(item == null){
            realm.executeTransactionAsync({ bgRealm ->

                val img = bgRealm.createObject(ImageBD::class.java, id)
                img.date = date
                img.thumbUrl = image.thumbUrl
                img.smallUrl = image.smallUrl
                img.word = et_search.text.toString()
            }, {
                // Transaction was a success.
                Log.e("Database", "Data Inserted")
            }, { error ->
                // Transaction failed and was automatically canceled.
                Log.e("Database", error.message)
            })
        }
        // ...а если подобная запись уже существует, мы просто делаем ей апдейт по времени, чтобы при сортировке
        // она находилась выше, чем была
        else {
            realm.beginTransaction()
            item.date = date
            item.thumbUrl = image.thumbUrl
            item.smallUrl = image.smallUrl
            realm.commitTransaction()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
