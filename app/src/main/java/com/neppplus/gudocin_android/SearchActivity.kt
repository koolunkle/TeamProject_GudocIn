package com.neppplus.gudocin_android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import com.mancj.materialsearchbar.MaterialSearchBar
import com.neppplus.gudocin_android.adapters.SuggestListAdapter
import com.neppplus.gudocin_android.databinding.ActivitySearchBinding
import com.neppplus.gudocin_android.datas.BasicResponse
import com.neppplus.gudocin_android.datas.ProductData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.LayoutInflater
import androidx.core.content.getSystemService as getSystemService1


class SearchActivity : BaseActivity() {

    lateinit var binding: ActivitySearchBinding

    var mSuggestList = ArrayList<ProductData>()

    lateinit var mSugestListAdapter : SuggestListAdapter

    var mInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

    lateinit var mSearchBar: MaterialSearchBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        setupEvents()
        setValues()

    }


    override fun setupEvents() {

        binding.btnCategriesEat.setOnClickListener {
            val myIntent = Intent(mContext, EatCategoryListActivity::class.java)

            startActivity(myIntent)
        }

        binding.btnCategriesWear.setOnClickListener {
            val myIntent = Intent(mContext, WearCategoryListActivity::class.java)
            myIntent.putExtra("Large_category_id", 1)
            startActivity(myIntent)
        }

        binding.btnCategriesLife.setOnClickListener {
            val myIntent = Intent(mContext, LifeCategoryListActivity::class.java)

            startActivity(myIntent)
        }
    }

    override fun setValues() {
        getProductListFromServer()
        binding.searchView.setMaxSuggestionCount(5)
        binding.searchView.setHint("생활에 필요한 구독을 검색하세요")
//        mSugestListAdapter.addSuggestion(ProductData("product_id", 100))


        mSugestListAdapter.setSuggestions(mSuggestList)
        binding.searchView.setCustomSuggestionAdapter(mSugestListAdapter)


        binding.searchResultListView.visibility = View.GONE

        binding.searchView.setOnSearchActionListener(object :
            MaterialSearchBar.OnSearchActionListener {


            override fun onSearchStateChanged(enabled: Boolean) {
                if (enabled) {
                    binding.searchResultListView.visibility = View.VISIBLE
                } else {
                    binding.searchResultListView.visibility = View.GONE
                }

            }

            override fun onSearchConfirmed(text: CharSequence?) {

            }

            override fun onButtonClicked(buttonCode: Int) {
//            if로 상품이 있으면 상품 띄워주고 없으면 없습니다 띄워주기


            }



        })

        binding.searchView.addTextChangeListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("LOG_TAG", " view text changed " + binding.searchView.getText())
                Log.d("LOG_TAG", " bar text changed " + mSearchBar.getText())

                mSugestListAdapter.getFilter().filter(binding.searchView.text)
//                text에 따라 추천 상품 바귀도록 아래 적기

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.searchResultListView.setOnItemClickListener(object :
            AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 아이템이 가지고 있는 대분류 카테고리로 그에 맞는 ListActivity로 보내줘야 함
                val myIntent = Intent(mContext, EatCategoryListActivity::class.java)
                startActivity(myIntent)
            }

        })


    }
    // 추천으로 띄워줄 아이템  API 로 호출해서 가져오기

    fun getProductListFromServer() {
        apiService.getRequestProductList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    val br = response.body()!!
                    mSuggestList.clear()
                    mSuggestList.addAll(br.data.products)



                    mSugestListAdapter.notifyDataSetChanged()

                }

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })
    }


}
