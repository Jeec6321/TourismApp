package com.android.tourismapp.Ui.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.tourismapp.R
import com.android.tourismapp.models.apiResponses.Review
import com.bumptech.glide.Glide

class ReviewAdapter (
    val reviews: ArrayList<Review>
): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        val ivPhoto = view.findViewById<AppCompatImageView>(R.id.iv_photo)
        val tvUser = view.findViewById<TextView>(R.id.tv_user)
        val tvRating = view.findViewById<TextView>(R.id.tv_rating)
        val tvDescription = view.findViewById<TextView>(R.id.tv_description)
        val clBackground = view.findViewById<ConstraintLayout>(R.id.cl_background)

        fun renderReview(review: Review) {
            tvUser.text = review.authorName

            tvDescription.text = review.text

            tvRating.text = view.resources.getString(R.string.rating_text, "" + review.rating)

            tvDescription.text = review.text

            if (review.profilePhotoUrl != null){
                Glide.with(view)
                    .load(review.profilePhotoUrl)
                    .circleCrop()
                    .into(ivPhoto)
            }

            clBackground.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(review.authorUrl))
                view.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ReviewViewHolder(
            layoutInflater.inflate(
                R.layout.row_review,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        println("Render")
        holder.renderReview(reviews[position])
    }

    override fun getItemCount(): Int {
        println("Size ${reviews.size}")
        return reviews.size
    }
}