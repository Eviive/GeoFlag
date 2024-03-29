package com.iut.geoflag.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iut.geoflag.R
import com.iut.geoflag.activities.DetailsActivity
import com.iut.geoflag.activities.MainActivity
import com.iut.geoflag.adapters.CountryAdapter
import com.iut.geoflag.databinding.FragmentHomeBinding
import com.iut.geoflag.models.Country

class HomeFragment(countries: List<Country>, launcher: ActivityResultLauncher<Intent>) : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val adapter = CountryAdapter(countries, seeDetail = {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("country", it)
        launcher.launch(intent)
    }) {
        val activity = requireActivity()

        if (activity is MainActivity) {
            activity.seeOnGoogleMaps(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupToolbar()

        binding.recyclerViewCountries.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.recyclerViewCountries.adapter = adapter

        return binding.root
    }

    private fun setupToolbar() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)

                val searchView = menu.findItem(R.id.toolbar_search_view).actionView as SearchView?

                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun scrollToTop() {
        adapter.scrollToTop()
    }

}