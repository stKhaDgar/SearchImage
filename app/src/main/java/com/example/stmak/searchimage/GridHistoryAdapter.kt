package com.example.stmak.searchimage

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.*
import com.example.stmak.searchimage.model.ImageBD
import com.squareup.picasso.Picasso

@Suppress("NAME_SHADOWING")
class GridHistoryAdapter(private val mContext: Context, private val items: ArrayList<ImageBD>) : BaseAdapter() {

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_history, null)

        val img: ImageView? = convertView!!.findViewById(R.id.item_img_history) as? ImageView
        val txtWork: TextView? = convertView.findViewById(R.id.word_use) as? TextView
        val item = items[position]

        Picasso.with(mContext).load(item.thumbUrl).into(img)
        txtWork?.text = item.word

        return convertView
    }
}