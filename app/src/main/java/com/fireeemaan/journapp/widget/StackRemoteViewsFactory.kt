package com.fireeemaan.journapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.ViewModelProvider
import com.fireeemaan.journapp.R
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.database.story.StoryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.net.HttpURLConnection
import java.net.URL

internal class StackRemoteViewsFactory(
    private val mContext: Context,
    private var widgetViewModel: WidgetViewModel
) : RemoteViewsService.RemoteViewsFactory {

    private val stories = mutableListOf<StoryEntity>()
    private lateinit var token: String

    override fun onCreate() {
        widgetViewModel =
            ViewModelProvider.AndroidViewModelFactory.getInstance(mContext.applicationContext as android.app.Application)
                .create(WidgetViewModel::class.java)

        val authPreferences =
            TokenDataStore.getInstance(mContext.dataStore)
        runBlocking {
            token = authPreferences.getAuthToken().first()
        }

        Log.e("STACKREMOTEVIEWS", "onCreate: $token")
    }

    override fun onDataSetChanged() {
        runBlocking {
            val allStories = widgetViewModel.getStories(token)
            stories.clear()
            if (allStories != null) {
                stories.addAll(allStories)
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val sortedStories = stories.sortedByDescending { it.createdAt }
        val story = sortedStories[position]
        val views = RemoteViews(mContext.packageName, R.layout.widget_item).apply {
            val bitmap = loadImageIntoWidget(story.photoUrl)
            bitmap?.let {
                setImageViewBitmap(R.id.image_view, it)
            }
        }

        val fillInIntent = Intent().apply {
            putExtra(ImageBannerWidget.EXTRA_ITEM, story.id)
        }
        views.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

        return views
    }

    override fun onDestroy() {}
    override fun getCount(): Int = stories.size
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true

    private fun loadImageIntoWidget(photoUrl: String): Bitmap? {
        return try {
            val url = URL(photoUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    companion object {
    }

}