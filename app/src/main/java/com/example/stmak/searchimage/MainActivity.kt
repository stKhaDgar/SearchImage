package com.example.stmak.searchimage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import org.json.JSONObject
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.TextView
import android.app.Activity
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {
    val items = ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidNetworking.initialize(applicationContext)

        onClickListener()

        grid_images.setOnItemClickListener { _, _, position, _ ->
            img_big.visibility = View.VISIBLE
            Picasso.with(this).load(items[position].regularUrl).into(img_big)
            grid_images.isEnabled = false
        }
    }

    private fun onClickListener() {
        button_search.setOnClickListener {
            use_search_tw.visibility = View.INVISIBLE
            getImages(et_search.text.toString())
        }

        img_big.setOnClickListener {
            grid_images.isEnabled = true
            img_big.visibility = View.INVISIBLE
        }

        et_search.setOnEditorActionListener( TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                use_search_tw.visibility = View.INVISIBLE
                getImages(et_search.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun getImages(word: String) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        items.clear()
        grid_images.adapter = null
        AndroidNetworking.get("https://api.unsplash.com/search/photos")
                .addQueryParameter("client_id", "dd867c1e011d5e088cba40b30db92299c48256a424fba6fa19fa931388bc0817")
                .addQueryParameter("page", "1")
                .addQueryParameter("per_page", "20")
                .addQueryParameter("query", word)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        for(i in 0..19) {
                            items.add(items.size, Image(response.getJSONArray("results").getJSONObject(i).getJSONObject("urls").getString("thumb"),
                                    response.getJSONArray("results").getJSONObject(i).getJSONObject("urls").getString("small")))

                        }
                        grid_images.adapter = GridMainAdapter(this@MainActivity, items)
                    }

                    override fun onError(error: ANError) {
                        Toast.makeText(this@MainActivity, error.errorDetail, Toast.LENGTH_SHORT).show()
                    }
                })
    }
}
