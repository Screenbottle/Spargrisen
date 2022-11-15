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

class MyAdapter (val inputList: MutableList<DatabaseController.Purchases> = DatabaseController().periodList):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val tvPurchaseName: TextView = itemView.findViewById(R.id.purchaseName)
        val tvPurchaseCategory: TextView = itemView.findViewById(R.id.purchaseCategory)
        val tvPurchasePrice: TextView = itemView.findViewById(R.id.price)
        //val tvPurchaseDate: TextView = itemView.findViewById(R.id.purchaseDate)
        //val tvKategorival: TextView = itemView.findViewById(R.id.spinnerC)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val itemView= LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvPurchaseName.text = inputList[position].purchaseName
        holder.tvPurchaseCategory.text = inputList[position].purchaseCategory
        holder.tvPurchasePrice.text = inputList[position].purchaseCost.toString()
       // holder.tvPurchaseDate.text = inputList[position].purchaseDate
       // holder.tvKategorival.text = inputList[position].kategorival.toString() //l

        //val inputText = inputList[position]
   

      //  if(inputText.purchaseCost!! >= 0){
      //     holder.tvPurchasePrice.text = "+ $%.2f".format(inputText.purchaseCost)
      //      holder.tvPurchasePrice.setTextColor(ContextCompat.getColor(context, R.color.green))
       // }else {
       //     holder.tvPurchasePrice.text = "- $%.2f".format(Math.abs(inputText.purchaseCost!!))
       //     holder.tvPurchasePrice.setTextColor(ContextCompat.getColor(context, R.color.red))
      //  }

       // holder.tvPurchaseName.text = inputText.purchaseName
    }

    override fun getItemCount(): Int {
        return inputList.size
    }

}
