package com.fireeemaan.journapp.ui.story.detail

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fireeemaan.journapp.data.datastore.TokenDataStore
import com.fireeemaan.journapp.data.datastore.dataStore
import com.fireeemaan.journapp.database.story.StoryEntity
import com.fireeemaan.journapp.databinding.FragmentDetailStoryBinding
import com.fireeemaan.journapp.ui.story.StoryViewModelFactory
import com.fireeemaan.journapp.data.Result

class DetailStoryFragment : Fragment() {

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    private val args: DetailStoryFragmentArgs by navArgs()

    private lateinit var imgStory: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDate: TextView

    private val viewModel: DetailStoryViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            requireActivity(),
            TokenDataStore.getInstance(requireContext().applicationContext.dataStore)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgStory = binding.ivDetailPhoto
        tvName = binding.tvDetailName
        tvDescription = binding.tvDetailDescription
        tvDate = binding.tvDetailDate

        showLoading(true)

        observeData()
    }

    private fun observeData() {
        viewModel.getStory(args.storyId).observe(viewLifecycleOwner) { story ->
            when (story) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    loadData(story.data)
                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(context, "Error: ${story.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadData(story: StoryEntity) {
        tvName.text = story.name
        tvDescription.text = story.description
        tvDate.text = story.createdAt

        Glide.with(this)
            .load(story.photoUrl)
            .into(imgStory)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}