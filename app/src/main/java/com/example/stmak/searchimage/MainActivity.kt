package com.example.stmak.searchimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidNetworking.initialize(applicationContext)

        findClickListener()


    }

    private fun findClickListener() {
        button_search.setOnClickListener {
            AndroidNetworking.get("https://api.unsplash.com/search/photos")
                    .addQueryParameter("client_id", "dd867c1e011d5e088cba40b30db92299c48256a424fba6fa19fa931388bc0817")
                    .addQueryParameter("page", "1")
                    .addQueryParameter("per_page", "1")
                    .addQueryParameter("query", "office")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            Toast.makeText(this@MainActivity, response.getString("total"), Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(error: ANError) {
                            Toast.makeText(this@MainActivity, error.errorDetail, Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }
}
