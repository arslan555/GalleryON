package com.arslan.data.repository
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.arslan.domain.model.album.AlbumItem
import com.arslan.domain.model.media.MediaItem
import com.arslan.domain.model.media.MediaType
import com.arslan.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val context: Context
) : MediaRepository {

    override fun getAlbums(): Flow<List<AlbumItem>> = flow {
        val albumMap = mutableMapOf<String, MutableList<MediaItem>>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.RELATIVE_PATH,
            MediaStore.Video.VideoColumns.DURATION // Video-specific
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateTaken = cursor.getLongOrNull(dateTakenColumn)
                val size = cursor.getLongOrNull(sizeColumn)
                val mediaTypeValue = cursor.getInt(mediaTypeColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val duration = cursor.getLongOrNull(durationColumn)

                val mediaType = when (mediaTypeValue) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaType.Image
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaType.Video
                    else -> continue
                }

                val contentUri = MediaStore.Files.getContentUri("external", id)

                val mediaItem = MediaItem(
                    id = id,
                    name = name,
                    uri = contentUri.toString(),
                    dateTaken = dateTaken,
                    mediaType = mediaType,
                    folderName = relativePath,
                    size = size,
                    duration = if (mediaType == MediaType.Video) duration else null
                )

                val albumKey = relativePath ?: "Unknown"
                val mediaList = albumMap.getOrPut(albumKey) { mutableListOf() }
                mediaList.add(mediaItem)
            }
        }

        val albums = albumMap.map { (folderName, mediaItems) ->
            AlbumItem(
                id = folderName,
                name = folderName.substringAfterLast("/").removeSuffix("/"),
                mediaItems = mediaItems.sortedByDescending { it.dateTaken }
            )
        }.sortedByDescending { it.mediaItems.maxOfOrNull { media -> media.dateTaken ?: 0L } }

        emit(albums)
    }

    override fun getMediaItems(albumId: String?): Flow<List<MediaItem>> = flow {
        val mediaItems = mutableListOf<MediaItem>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.RELATIVE_PATH,
            MediaStore.Video.VideoColumns.DURATION
        )

        val selectionBuilder = StringBuilder()
        val selectionArgs = mutableListOf<String>()

        selectionBuilder.append(
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"
        )
        selectionArgs.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
        selectionArgs.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

        if (albumId != null) {
            selectionBuilder.append(" AND ${MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ?")
            selectionArgs.add("%$albumId%")
        }

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selectionBuilder.toString(),
            selectionArgs.toTypedArray(),
            "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateTaken = cursor.getLongOrNull(dateTakenColumn)
                val size = cursor.getLongOrNull(sizeColumn)
                val mediaTypeValue = cursor.getInt(mediaTypeColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val duration = cursor.getLongOrNull(durationColumn)

                val mediaType = when (mediaTypeValue) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaType.Image
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaType.Video
                    else -> continue
                }

                val contentUri = MediaStore.Files.getContentUri("external", id)

                mediaItems.add(
                    MediaItem(
                        id = id,
                        name = name,
                        uri = contentUri.toString(),
                        dateTaken = dateTaken,
                        mediaType = mediaType,
                        folderName = relativePath,
                        size = size,
                        duration = if (mediaType == MediaType.Video) duration else null
                    )
                )
            }
        }

        emit(mediaItems)
    }


    private fun Cursor.getLongOrNull(columnIndex: Int): Long? {
        return if (isNull(columnIndex)) null else getLong(columnIndex)
    }
}