package com.example.spargrisen

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spargrisen.fragments.InputText
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter (val context: Context, val inputList: List<InputText>):  RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var db: FirebaseFirestore

        val IteamName: TextView = itemView.findViewById(R.id.purchaseName)
        val IteamCategory: TextView = itemView.findViewById(R.id.purchaseCategory)
        val IteamPrice: TextView = itemView.findViewById(R.id.price)
        val IteamDate: TextView = itemView.findViewById(R.id.purchaseDate)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val itemView= LayoutInflater.from(context).inflate(R.layout.category_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.IteamName.text = inputList[position].purchaseName
        holder.IteamCategory.text = inputList[position].purchaseCategory
        holder.IteamPrice.text = inputList[position].purchaseDate
        holder.IteamDate.text = inputList[position].purchaseDate

    }

    override fun getItemCount(): Int {
    return inputList.size
    }

}