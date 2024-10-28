package com.example.echonote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

enum class Page { HOME, ADD, TEST }

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        viewPager.adapter = ViewPagerAdapter(this)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    viewPager.currentItem = Page.HOME.ordinal
                    true
                }
                R.id.nav_test -> {
                    viewPager.currentItem = Page.TEST.ordinal
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener {
            viewPager.currentItem = Page.ADD.ordinal
            bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = false
            bottomNavigationView.menu.findItem(R.id.nav_test).isChecked = false
            bottomNavigationView.menu.findItem(R.id.nav_add).isChecked = false
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (Page.entries[position]) {
                    Page.HOME -> bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
                    Page.TEST -> bottomNavigationView.menu.findItem(R.id.nav_test).isChecked = true
                    Page.ADD -> {
                        bottomNavigationView.menu.findItem(R.id.nav_add).isChecked = true
                    }
                }
            }
        })
    }
}
