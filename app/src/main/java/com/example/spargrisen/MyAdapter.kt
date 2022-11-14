package com.example.spargrisen

import android.annotation.SuppressLint
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(val inputList: MutableList<DatabaseController.Purchases>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        lateinit var db: FirebaseFirestore

        val tvPurchaseName: TextView = itemView.findViewById(R.id.purchaseName)
        val tvPurchaseCategory: TextView = itemView.findViewById(R.id.purchaseCategory)
        val tvPurchasePrice: TextView = itemView.findViewById(R.id.price)
        val tvPurchaseDate: TextView = itemView.findViewById(R.id.purchaseDate)
        //val tvKategorival: TextView = itemView.findViewById(R.id.spinnerC)


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val itemView= LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvPurchaseName.text = DatabaseController().purchasesList[position].purchaseName
        holder.tvPurchaseCategory.text = DatabaseController().purchasesList[position].purchaseCategory
        holder.tvPurchasePrice.text = DatabaseController().purchasesList[position].purchaseCost.toString()
        holder.tvPurchaseDate.text = DatabaseController().purchasesList[position].purchaseDateString
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
