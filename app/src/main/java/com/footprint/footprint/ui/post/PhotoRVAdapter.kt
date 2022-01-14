package com.footprint.footprint.ui.post

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.footprint.footprint.databinding.ItemPhotoBinding
import kotlin.collections.ArrayList

class PhotoRVAdapter(): RecyclerView.Adapter<PhotoRVAdapter.ViewHolder>() {
    interface MyItemClickListener {
        fun goGalleryClick()
    }

    private lateinit var binding: ItemPhotoBinding
    private lateinit var myItemClickListener: MyItemClickListener

    private val imgList: ArrayList<Uri> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoRVAdapter.ViewHolder {
        binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoRVAdapter.ViewHolder, position: Int) {
        holder.photoIv.setImageURI(imgList[position])
        holder.goGalleryView.setOnClickListener {
            myItemClickListener.goGalleryClick()
        }
    }

    override fun getItemCount(): Int = imgList.size

    fun addData(imgList: ArrayList<Uri>) {
        this.imgList.clear()
        this.imgList.addAll(imgList)
        notifyDataSetChanged()
    }

    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    inner class ViewHolder(itemView: ItemPhotoBinding): RecyclerView.ViewHolder(itemView.root) {
        val photoIv: ImageView = itemView.photoPhotoIv
        val goGalleryView: View = itemView.photoGoGalleryView
    }
}