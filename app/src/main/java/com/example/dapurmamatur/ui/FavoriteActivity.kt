package com.example.dapurmamatur.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dapurmamatur.data.repository.MainRepository
import com.example.dapurmamatur.databinding.ActivityFavoriteBinding
import com.example.dapurmamatur.di.ApiModule
import com.example.dapurmamatur.di.DbModule
import com.example.dapurmamatur.ui.adapter.FavoriteAdapter
import com.example.dapurmamatur.R
import com.example.dapurmamatur.viewmodel.FavoriteViewModel
import com.example.dapurmamatur.viewmodel.FavoriteViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = MainRepository(
            ApiModule.provideApiService(),
            DbModule.provideFoodDao(DbModule.provideDatabase(applicationContext))
        )

        favoriteViewModel = ViewModelProvider(this, FavoriteViewModelFactory(repository))[FavoriteViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()

        binding.backButtonFavorite.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter().apply {
            setOnItemClickListener { foodEntity ->
                val intent = Intent(this@FavoriteActivity, DetailActivity::class.java).apply {
                    putExtra("MEAL_ID", foodEntity.id)
                }
                startActivity(intent)
            }
        }
        binding.recyclerViewRecipes.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = favoriteAdapter
        }
    }

    private fun setupObservers() {
        favoriteViewModel.favoriteList.observe(this) {
            favoriteAdapter.setData(it)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_favorite -> true
                R.id.navigation_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigation.selectedItemId = R.id.navigation_favorite
    }
}
