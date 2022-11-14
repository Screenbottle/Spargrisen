package com.example.spargrisen

import android.annotation.SuppressLint
import android.graphics.Color.red
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spargrisen.fragments.InputText
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter (val inputList: ArrayList<InputText>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        lateinit var db: FirebaseFirestore

        val tvPurchaseName: TextView = itemView.findViewById(R.id.purchaseName)
        val tvPurchaseCategory: TextView = itemView.findViewById(R.id.purchaseCategory)
        val tvPurchasePrice: TextView = itemView.findViewById(R.id.price)
        val tvPurchaseDate: TextView = itemView.findViewById(R.id.purchaseDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvPurchaseName.text = inputList[position].purchaseName
        holder.tvPurchaseCategory.text = inputList[position].purchaseCategory
        holder.tvPurchasePrice.text = inputList[position].purchaseCost.toString()

    }

    override fun getItemCount(): Int {
        return inputList.size
    }

}