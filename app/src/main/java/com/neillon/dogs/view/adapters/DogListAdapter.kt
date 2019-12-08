package com.neillon.dogs.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.neillon.dogs.R
import com.neillon.dogs.databinding.DogListItemBinding
import com.neillon.dogs.model.Dog
import com.neillon.dogs.view.DogItemClickListener
import com.neillon.dogs.view.fragments.ListFragmentDirections
import com.neillon.dogs.view.holders.DogViewHolder
import kotlinx.android.synthetic.main.dog_list_item.view.*

class DogListAdapter(val dogList: ArrayList<Dog>) : RecyclerView.Adapter<DogViewHolder>(),
    DogItemClickListener {

    fun updateDogList(newDogList: ArrayList<Dog>) {
        dogList.clear()
        dogList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<DogListItemBinding>(
            inflater,
            R.layout.dog_list_item,
            parent,
            false
        )
        return DogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dogList.size
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog = dogList[position]
        holder.view.dogItemClickListener = this
    }

    override fun onDogItemClick(v: View) {
        val uuid = v.dogId.text.toString().toInt()
        val action = ListFragmentDirections.actionDetailFragment()
        action.dogUUId = uuid
        Navigation.findNavController(v).navigate(action)
    }

}