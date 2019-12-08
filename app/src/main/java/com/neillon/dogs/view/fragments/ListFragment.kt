package com.neillon.dogs.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.neillon.dogs.R
import com.neillon.dogs.model.Dog
import com.neillon.dogs.view.adapters.DogListAdapter
import com.neillon.dogs.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private lateinit var viewModel: ListViewModel
    private val dogListAdapter = DogListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        recyclerViewDogs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }

        refreshLayoutDogs.setOnRefreshListener {
            recyclerViewDogs.visibility = View.GONE
            listError.visibility = View.GONE
            progressLoadingDogs.visibility = View.VISIBLE
            viewModel.refreshByPassCache()
            refreshLayoutDogs.isRefreshing = false
        }

        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings -> {
                view?.let {
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionSettings())
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(this, Observer { dogs: List<Dog> ->
            dogs?.let {
                recyclerViewDogs.visibility = View.VISIBLE
                dogListAdapter.updateDogList(dogs as ArrayList<Dog>)
            }
        })

        viewModel.dogsLoadError.observe(this, Observer {
            it?.let {
                listError.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(this, Observer {
            it?.let {
                progressLoadingDogs.visibility = if (it) View.VISIBLE else View.GONE
                if (it) {
                    listError.visibility = View.GONE
                    recyclerViewDogs.visibility = View.GONE
                }
            }
        })
    }
}
