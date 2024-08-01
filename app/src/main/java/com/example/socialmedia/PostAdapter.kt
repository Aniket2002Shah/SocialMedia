package com.example.socialmedia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.model.Post
import com.example.socialmedia.util.Util
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>,private val mContext: OnPostItemClicked): FirestoreRecyclerAdapter<Post, PostAdapter.PostItemHolder>(options)
{

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemHolder {
                val postView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_post, parent, false)!!
                val postHolder = PostItemHolder(postView)
                postHolder.likeImg.setOnClickListener {
                    mContext.onLikedButtonClicked(snapshots.getSnapshot(postHolder.absoluteAdapterPosition).id)
                }
                postHolder.share.setOnClickListener {
                    mContext.onShareButton(snapshots.getSnapshot(postHolder.absoluteAdapterPosition).id)
                }
            return postHolder
        }

        override fun onBindViewHolder(holder: PostItemHolder, position: Int, model: Post) {

            Glide.with(holder.profilePicture.context).load(model.createdBy.image).circleCrop()
                .into(holder.profilePicture)

            holder.userName.text = model.createdBy.name
            holder.time.text = Util.getTimeAgo(model.createdAt)
            holder.description.text = model.description
            holder.like.text = "${model.likedBy.size} likes"


            val authId= Firebase.auth.currentUser!!.uid
            if(model.likedBy.contains(authId)){
              holder.likeImg.setImageDrawable(ContextCompat.getDrawable(holder.likeImg.context,R.drawable.baseline_thumb_up_24))
            }
            else{
                holder.likeImg.setImageDrawable(ContextCompat.getDrawable(holder.likeImg.context,R.drawable.baseline_thumb_up_25))
            }
        }

    inner class PostItemHolder(item: View) : RecyclerView.ViewHolder(item) {

        val profilePicture = item.findViewById<ImageView>(R.id.profilePic)!!
        val userName = item.findViewById<TextView>(R.id.userName)!!
        val time = item.findViewById<TextView>(R.id.time)!!
        val description = item.findViewById<TextView>(R.id.post)!!
        val likeImg = item.findViewById<ImageView>(R.id.likeImg)!!
        val like = item.findViewById<TextView>(R.id.likes)!!
        val share = item.findViewById<ImageView>(R.id.shareButton)!!

    }

    interface OnPostItemClicked{
        fun onLikedButtonClicked(postId:String)
        fun onShareButton(postId: String)

    }

}