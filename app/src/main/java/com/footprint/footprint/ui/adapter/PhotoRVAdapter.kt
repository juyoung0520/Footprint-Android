package com.footprint.footprint.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.footprint.footprint.databinding.ItemPhotoBinding

//사진 뷰페이저 어댑터
//산책 중지 에서도 사용되고, 글 작성하기에서도 사용됨.
//layout==0 -> 글 작성하기 화면, layout==1 -> 산책 중지 화면
class PhotoRVAdapter(private val layout: Int) : RecyclerView.Adapter<PhotoRVAdapter.ViewHolder>() {
    interface MyItemClickListener { //글 작성하기 화면에서 사용
        fun goGalleryClick()
    }

    private lateinit var binding: ItemPhotoBinding
    private lateinit var myItemClickListener: MyItemClickListener

    private val imgList: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoRVAdapter.ViewHolder {
        binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoRVAdapter.ViewHolder, position: Int) {
        //이미지
        if (imgList[position].startsWith("https://"))
            Glide.with(holder.itemView).load(imgList[position]).into(holder.photoIv)
        else
            holder.photoIv.setImageURI(Uri.parse(imgList[position]))

        if (layout == 0) {    //글 작성하기 화면일 때
            holder.goGalleryView.visibility = View.VISIBLE  //갤러리 이동
            holder.editPhotoIv.visibility = View.VISIBLE    //갤러리 이동

            holder.goGalleryView.setOnClickListener {   //갤러리 이동 클릭 리스너
                myItemClickListener.goGalleryClick()
            }
        } else {    //산책 중지 화면일 때
            holder.goGalleryView.visibility = View.INVISIBLE    //갤러리 이동
            holder.editPhotoIv.visibility = View.INVISIBLE  //갤러리 이동
        }
    }

    override fun getItemCount(): Int = imgList.size

    //글 작성하기 화면에서 사용
    fun addImgList(imgList: ArrayList<String>) {
        this.imgList.clear()
        this.imgList.addAll(imgList)
        notifyDataSetChanged()
    }

    fun clearImgList() {
        this.imgList.clear()
        notifyDataSetChanged()
    }

    //글 작성하기 화면에서 사용
    fun setMyItemClickListener(myItemClickListener: MyItemClickListener) {
        this.myItemClickListener = myItemClickListener
    }

    inner class ViewHolder(itemView: ItemPhotoBinding) : RecyclerView.ViewHolder(itemView.root) {
        val photoIv: ImageView = itemView.photoPhotoIv
        val editPhotoIv: ImageView = itemView.photoEditPhotoIv
        val goGalleryView: View = itemView.photoGoGalleryView
    }
}