package edu.stanford.joseph72.myyelp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "RI2VKndYwRKTvZrTwEY_O3MN-UazLtVHkVFuF1DBO3_Sb_4Ymn7W175SoGY6VyYnwr_tOwpxgN0EJJuwyGuTNDyuiIN7S4gmTGsLhaM9xLxOF3Xskj5wey2al3STYXYx"

class MainActivity : AppCompatActivity() {
    private lateinit var rvRestaurants : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvRestaurants = findViewById(R.id.rvRestaurants)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.searchRestaurants("Bearer $API_KEY", "Avocado Toast", "New York").enqueue(object : Callback<YelpSearchResult>{
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from Yelp API... exiting")
                    return
                }
                restaurants.addAll(body.restaurants)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
    }
}