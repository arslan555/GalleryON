package com.arslan.feature.media.presentation.mediadetail

import androidx.lifecycle.ViewModel
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.usecase.mediadetails.GetMediaDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    private val mediaDetailUseCase: GetMediaDetailUseCase
) : ViewModel() {
    fun getMediaItem(mediaId: Long): Flow<MediaItem?> {
        return mediaDetailUseCase.execute(mediaId)
    }
}