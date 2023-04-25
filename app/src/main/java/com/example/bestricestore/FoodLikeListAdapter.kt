package com.example.bestricestore
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bestricestore.data.FoodEntity
import com.example.bestricestore.databinding.ListFoodLikeBinding

class FoodLikeListAdapter constructor(
    private val context: Context,
    private var foodList: List<FoodEntity>,
    private val listener: ListFoodLikeListener
) : RecyclerView.Adapter<FoodLikeListAdapter.FoodViewHolder>() {
    interface ListFoodLikeListener {
        fun onItemClick(foodId: String?)
    }

    inner class FoodViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodViewBinding: ListFoodLikeBinding

        init {
            foodViewBinding = ListFoodLikeBinding.bind(itemView) //initiate binding. pass the view to binding
        }

        fun bindData(fData: FoodEntity) {
            val sale: Int = 100 - fData.salePercent
            foodViewBinding.foodName.text = fData.name
            foodViewBinding.foodCost.text = "Price:" + fData.cost + " VND"
            foodViewBinding.root
                .setOnClickListener({ v: View? -> listener.onItemClick(fData.id) })

            if (sale != 100) {
                foodViewBinding.foodCost.paintFlags = foodViewBinding.foodCost.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                foodViewBinding.foodCostSale.visibility = View.VISIBLE
                foodViewBinding.foodCostSale.text = "SalePrice: " + (fData.cost * sale / 100
                        ) + " (" + fData.salePercent + "%)"
            }
            foodViewBinding.foodTotalLike.text = "Total like : ${fData.totalLike}"
            Glide.with(context).load(fData.imageUrl)
                .error(R.drawable.test)
//                .apply(RequestOptions().override(600, 180)).fitCenter()
                .into(foodViewBinding.foodImage)
        }
    }

    fun filterList(filterList: List<FoodEntity>) {
        foodList = filterList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodLikeListAdapter.FoodViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_food_like, parent, false) //create view for each of food
        //not only view, we need ViewHolder, so
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val fData: FoodEntity = foodList[position]
        holder.bindData(fData)     }


    override fun getItemCount(): Int {
        return foodList.size
    }
}