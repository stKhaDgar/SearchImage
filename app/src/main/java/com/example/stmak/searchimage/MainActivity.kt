package com.example.stmak.searchimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter




class MainActivity : AppCompatActivity() {
    val items = ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidNetworking.initialize(applicationContext)

        onClickListener()


    }

    private fun onClickListener() {
        button_search.setOnClickListener {
            getImages(et_search.text.toString())
        }
    }

    private fun getImages(word: String) {
        AndroidNetworking.get("https://api.unsplash.com/search/photos")
                .addQueryParameter("client_id", "dd867c1e011d5e088cba40b30db92299c48256a424fba6fa19fa931388bc0817")
                .addQueryParameter("page", "1")
                .addQueryParameter("per_page", "10")
                .addQueryParameter("query", word)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        for(i in 0..9) {
                            items.add(items.size, Image(response.getJSONArray("results").getJSONObject(i).getJSONObject("urls").getString("thumb"),
                                    response.getJSONArray("results").getJSONObject(i).getJSONObject("urls").getString("regular")))

                        }

                        grid_images.adapter = GridMainAdapter(this@MainActivity, items)
                    }

                    override fun onError(error: ANError) {
                        Toast.makeText(this@MainActivity, error.errorDetail, Toast.LENGTH_SHORT).show()
                    }
                })
    }
}
