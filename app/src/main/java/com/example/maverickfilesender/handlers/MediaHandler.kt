package com.example.maverickfilesender.handlers

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import com.example.maverickfilesender.model.Image
import com.example.maverickfilesender.model.Music
import com.example.maverickfilesender.model.Video
import java.lang.Exception

class MediaHandler(val context: Context) {

    val imageCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        }

    val imageProjection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Video.Media.DATA

    )

    val videoCollection=  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    }


    val videoProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION

            )



    val audioCollection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }


    val audioProjection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.ALBUM,





    )




    val imageSortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"


    val videoSortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"




    fun getMediaFromDevice(): ArrayList<Music> {
        var query: Cursor?=null



            query = context.contentResolver.query(audioCollection, audioProjection, null, null,null)



        val musicList = ArrayList<Music>()


        query.use { cursor ->


            var idColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            var nameColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            var durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            var sizeColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            var artistColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            var dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            var albumColumn=cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)




            while (cursor!!.moveToNext()) {
                val id = cursor.getLong(idColumn!!)
                val name = cursor.getString(nameColumn!!)
                val duration = cursor.getInt(durationColumn!!)
                val size = cursor.getInt(sizeColumn!!)
                var artist = cursor.getString(artistColumn!!)
                val pathData = cursor.getString(dataColumn!!)
                val album=cursor.getString(albumColumn!!)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )


                var bitmap: Bitmap? = null

                try {
                    var mediaMetaDataRetriever = MediaMetadataRetriever()
                    mediaMetaDataRetriever.setDataSource(pathData)
                    val data = mediaMetaDataRetriever.embeddedPicture
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)

                    mediaMetaDataRetriever.release()
                } catch (e: Exception) {
                    bitmap = null
                }


                /*
            val artworkUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtUri = ContentUris.withAppendedId(artworkUri, id)


             */


try{
    musicList.add(Music(contentUri, id, name, artist,album, bitmap, duration, size,pathData,false,null))
}catch (e:Exception){

}

            }








            return musicList
        }


    }



    fun fetchImageFiles() : ArrayList<Image>{

        val query = context.contentResolver.query(
            imageCollection, imageProjection,
            null,
            null,
            imageSortOrder
        )

val imageList=ArrayList<Image>()


        query.use { cursor ->

            cursor!!.moveToFirst()

            val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val dataColumn=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
 while (cursor.moveToNext()){

     val id=cursor.getLong(idColumn)
     val name=cursor.getString(nameColumn)
     val size=cursor.getInt(sizeColumn)
     val date=cursor.getInt(dateAddedColumn)
val path=cursor.getString(dataColumn)

val contentUri=ContentUris.withAppendedId( MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),id)




 //    var bitmap:Bitmap?=null

// if(Build.VERSION.SDK_INT>=28){
//try {
//    val source = ImageDecoder.createSource(context.contentResolver, contentUri)
//    bitmap = ImageDecoder.decodeBitmap(source)
//
//}
//catch (e:Exception){
//bitmap=null
//}



// }
//     else{
//
//bitmap=MediaStore.Images.Media.getBitmap(context.contentResolver,contentUri)
//
// }


     try{
         imageList.add(Image(id, name, size, date, contentUri, path, null, false))
     }
     catch (e:Exception) {

     }

 }

        }

return imageList
    }

    fun fetchVideoFiles():ArrayList<Video> {

        val query = context.contentResolver.query(
                videoCollection, videoProjection,
                null,
                null,
                videoSortOrder
        )

        val videoList=ArrayList<Video>()


        query.use { cursor ->


            cursor!!.moveToFirst()

            val idColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val dataColumn=cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
val durationColumn=cursor.getColumnIndex(MediaStore.Video.Media.DURATION)

            while (cursor.moveToNext()){



                val id=cursor.getLong(idColumn)
                val name=cursor.getString(nameColumn)
                val size=cursor.getInt(sizeColumn)
                val date=cursor.getInt(dateAddedColumn)
val data=cursor.getString(dataColumn)
val duration=cursor.getInt(durationColumn)
                val contentUri=ContentUris.withAppendedId( MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),id)

                val mPath=contentUri.path


var bitmap:Bitmap?=null

var durationStr=""
                var allSeconds=duration/1000
                var seconds=0
                var allMinutes=0
                var hours=0
                var minutes=0
                if(allSeconds>60){

                    allMinutes=allSeconds/60
                    seconds=allSeconds%60

                    val secondsStr=if(seconds>9) "${seconds.toString()}" else "0${seconds.toString()}"

                    durationStr=if(allMinutes>9) "${allMinutes.toString()}:$secondsStr" else "0${allMinutes.toString()}:$secondsStr"
                }
                else{
                    durationStr=if(allSeconds>9) "00:${allSeconds.toString()}" else "00:0${allSeconds.toString()}"
                }


                if(allMinutes>60){
                    hours=allMinutes/60
                    minutes=allMinutes%60


                    val minutesStr=if(seconds>9) "${minutes.toString()}" else "0${minutes.toString()}"

                    durationStr=if(hours>9) "${hours.toString()}:$minutesStr" else "0${hours.toString()}:$minutesStr"

                }



//try {
////    val metaDataReceiver = MediaMetadataRetriever()
////    metaDataReceiver.setDataSource(data)
////    bitmap = metaDataReceiver.frameAtTime
//
//    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//        bitmap=context.contentResolver.loadThumbnail(contentUri, Size(640,480),null)
//    }
//}
//catch (e:Exception){
//    val msg=e.message
//    bitmap=null
//}
//

//bitmap=ThumbnailUtils.createVideoThumbnail(data,MediaStore.Video.Thumbnails.MICRO_KIND)

try {
    videoList.add(Video(id, name, size, date, contentUri, data, duration, durationStr, null, bitmap, false))
}
catch (e:Exception){

}

            }

        }

        return videoList
    }


}