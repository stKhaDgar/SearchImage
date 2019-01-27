package com.st.maktavish.freedomimages

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import com.squareup.picasso.Picasso


@Suppress("NAME_SHADOWING")
class GridMainAdapter(private val mContext: Context, private val items: ArrayList<Image>) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_main, null)


        val img: ImageView? = convertView!!.findViewById(R.id.item_img) as? ImageView
        val urlImg = items[position]

        // Именно здесь происходит то, что я описывал в MainActivity.kt . Выше мы вытянули нашу картинку с различными
        // размерами и подставляем в метод .load() её в низком варианте качества (thumb) и соответственно подгружаем
        // при помощи библиотеки Picasso
        Picasso.with(mContext).load(urlImg.thumbUrl).into(img)

        return convertView
    }
}