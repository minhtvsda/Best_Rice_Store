package com.example.bestricestore
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bestricestore.data.FoodEntity
import com.example.bestricestore.databinding.ListFoodSoldBinding

class FoodSoldListAdapter constructor(
    private val context: Context,
    private var foodList: List<FoodEntity>,
    private val listener: ListFoodSoldListener
) : RecyclerView.Adapter<FoodSoldListAdapter.FoodViewHolder>() {
    interface ListFoodSoldListener {
        fun onItemClick(foodId: String?)
    }

    inner class FoodViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodViewBinding: ListFoodSoldBinding

        init {
            foodViewBinding =
                ListFoodSoldBinding.bind(itemView) //initiate binding. pass the view to binding
        }

        fun bindData(fData: FoodEntity) {
            val sale: Int = 100 - fData.salePercent
            foodViewBinding.foodName.setText(fData.name)
            foodViewBinding.foodCost.setText("Price:" + fData.cost + " VND")
            foodViewBinding.getRoot()
                .setOnClickListener(View.OnClickListener({ v: View? -> listener.onItemClick(fData.id) }))
            if (sale != 100) {
                foodViewBinding.foodCost.setPaintFlags(foodViewBinding.foodCost.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                foodViewBinding.foodCostSale.setVisibility(View.VISIBLE)
                foodViewBinding.foodCostSale.setText(
                    "SalePrice: " + (fData.cost * sale / 100
                            ) + " (" + fData.salePercent + "%)"
                )
            }
            foodViewBinding.foodTotalSold.text = "Total sold : ${fData.totalSell}"
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

    public override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodSoldListAdapter.FoodViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_food_sold, parent, false) //create view for each of food
        //not only view, we need ViewHolder, so
        return FoodViewHolder(view)
    }

    public override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val fData: FoodEntity = foodList.get(position)
        holder.bindData(fData)
    }

    public override fun getItemCount(): Int {
        return foodList.size
    }
}