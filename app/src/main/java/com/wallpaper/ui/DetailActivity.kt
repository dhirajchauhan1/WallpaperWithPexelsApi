package com.wallpaper.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wallpaper.databinding.ActivityDetailBinding
import com.wallpaper.db.MainDatabase
import com.wallpaper.models.Photo
import com.wallpaper.repository.MainRepository
import es.dmoral.toasty.Toasty
import kotlin.math.abs


class DetailActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityDetailBinding
    val TAG = "Detail"
    lateinit var gestureDetector: GestureDetector
    var x2:Float = 0.0f
    var x1:Float = 0.0f
    var y2:Float = 0.0f
    var y1:Float = 0.0f

    var position:Int = 0
    lateinit var photoList: List<Photo>

    var downloadId: Long = 0

    companion object{
        const val MIN_DISTANCE = 15
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gestureDetector = GestureDetector(this, this)
        binding.photoView.setOnTouchListener { view, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
        }

        val repository = MainRepository(MainDatabase(this))
        val viewModelProviderFactory = ViewModelProviderFactory( application,repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)

        val intent: Intent = this.intent
        val myJsonData = intent.getStringExtra("jsonPhoto")
        position = intent.getIntExtra("position", 0)
        val gson = Gson()
        val type = object : TypeToken<List<Photo>?>() {}.type
        photoList = gson.fromJson(myJsonData, type)

        setPhoto(position)

        binding.save.setOnClickListener { view->
            viewModel.savePhoto(photoList[position])
            Snackbar.make(view, "Photo Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }

        binding.download.setOnClickListener {
            val photo:Photo = photoList[position]
            downloadPhoto(photo.src.large2x, photo.id)
        }
        downloadStatus()

    }

    fun setPhoto(position: Int){
        if (photoList.isNotEmpty()){
            val photo:Photo = photoList[position]

            Glide.with(this@DetailActivity).load(photo.src.large2x)
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.photoView)
        }
    }

    fun downloadPhoto(url: String, id:Int){
        val src_uri: Uri = Uri.parse(url)
        val req = DownloadManager.Request(src_uri)
            .setDestinationInExternalPublicDir("Wallpaper", "$id Wallpaper.PNG")
            .setTitle("Image from Wallpaper")
            .setDescription("Image from Wallpaper downloading..")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)

        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
       downloadId = dm.enqueue(req)
    }

    fun downloadStatus(){

        var br = object: BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
               var id : Long? = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)

                if (id == downloadId){
                    Toasty.success(this@DetailActivity, "Image Download Successfully ").show()
                }
            }
        }

        registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        gestureDetector.onTouchEvent(event)

        when(event?.action){

            // when we start swipe
            0 -> {
                x1 = event.x
                y1 = event.y
            }

            // when we end swipe
            1 -> {
                x2 = event.x
                y2 = event.y

                val valueX: Float = x2 - x1
                val valueY: Float = y2 - y1

                if (abs(valueX) > MIN_DISTANCE) {
                    if (x2 > x1) {
                        Log.d(TAG, "Right Swipe")
                        if (position > 0) {
                            position--
                        }
                        setPhoto(position)
                    } else {
                        Log.d(TAG, "Left Swipe")

                        if (position != photoList.size - 1) {
                            position++
                        }
                        setPhoto(position)
                    }
                } else if (abs(valueY) > MIN_DISTANCE) {
                    if (y2 > y1) {
                        Log.d(TAG, "Bottom Swipe")
                    } else {
                        Log.d(TAG, "Top Swipe")
                    }
                }

            }
        }

        return super.onTouchEvent(event)
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    override fun onDown(p0: MotionEvent?): Boolean {
      return false
    }

    override fun onShowPress(p0: MotionEvent?) {

    }

    override fun onSingleTapUp(event: MotionEvent?): Boolean {

        return false
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {

    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

}