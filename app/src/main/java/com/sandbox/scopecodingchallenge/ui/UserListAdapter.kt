package com.sandbox.scopecodingchallenge.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sandbox.scopecodingchallenge.R
import com.sandbox.scopecodingchallenge.model.Data

class UserListAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {
    private var userArray = arrayListOf<Data>()

    fun setList(userList: List<Data>?) {
        userArray.clear()

        if (userList != null)
            userArray.addAll(userList.filter { it.userid != null })

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userArray[position])
    }

    override fun getItemCount(): Int = userArray.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userPicture: ImageView = itemView.findViewById(R.id.userPicture)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val userVehicleCount: TextView = itemView.findViewById(R.id.userCarCount)

        fun bind(data: Data) {
            userName.text = itemView.resources.getString(R.string.main_activity_user_name, data.owner.name, data.owner.surname)
            userVehicleCount.text = itemView.resources.getString(R.string.main_activity_vehicle_count, data.vehicles.size)

            Glide.with(itemView.context)
                .load(data.owner.foto)
                .placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_off_24)
                .into(userPicture)

            itemView.setOnClickListener {
                itemClickListener.onItemClick(data)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Data)
    }
}