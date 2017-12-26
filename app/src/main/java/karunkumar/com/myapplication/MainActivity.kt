package karunkumar.com.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.Toast
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*



class MainActivity : Activity() {


    private var imageLoaders: ImageLoader? = null
    private var loadedImages: Bitmap? = null
    internal var url = "https://i.pinimg.com/736x/3b/f3/1a/3bf31abc7a7a95f381b21a1d8a7ad10f--charlotte-rampling-rose-flower.jpg"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.imageLoaders = ImageLoader.getInstance()


        imageLoaders!!.loadImage(url, object : SimpleImageLoadingListener() {
            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImagee: Bitmap?) {
                // image.setImageBitmap(loadedImagee)
                loadedImages =getResizedBitmap(loadedImagee!!,400)
            } })



        btn.setOnClickListener({

         ShareImage("com.android.mms", " Developer", "gmail")
          // ShareImage("com.facebook.katana", " Developer", "gmail")
           // ShareImage("com.google.android.gm", " Developer", "gmail")
            //ShareImage("com.facebook.lite", " Developer", "facebook")
           // ShareImage("com.whatsapp", "fk", "whatsapp")

           // ShareImageOther("")

        })

    }

    @Throws(IOException::class)
    private fun  ShareImageOther(name:String){

        val sharedFile = createFile()
        val uri = FileProvider.getUriForFile(this, SHARED_PROVIDER_AUTHORITY, sharedFile)
        val intentBuilder = ShareCompat.IntentBuilder.from(this)
                .setType("image/*")
                .addStream(uri)
        val chooserIntent = intentBuilder.createChooserIntent()
        startActivity(chooserIntent)

    }

    private fun ShareImage(package_name:String, name:String, name_type:String){

        val sharedFile = createFile()
        val uri = FileProvider.getUriForFile(this, SHARED_PROVIDER_AUTHORITY, sharedFile)

        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/*"
        intent.`package` = package_name
        intent.putExtra(Intent.EXTRA_TEXT, name)
        try {
            this.startActivity(intent)
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(applicationContext, name_type + " have not been installed.",Toast.LENGTH_SHORT).show()
        }

    }



    @Throws(IOException::class)
    private fun createFile(): File {

            val sharedFolder = File(filesDir, SHARED_FOLDER)
        sharedFolder.mkdirs()

        val sharedFile = File.createTempFile("picture", ".png", sharedFolder)
        sharedFile.createNewFile()

        writeBitmap(sharedFile, loadedImages!!)
        return sharedFile
    }
    companion object {

        private val SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".myfileprovider"
        private val SHARED_FOLDER = "shared"

        private fun writeBitmap(destination: File, bitmap: Bitmap) {
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(destination)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                close(outputStream)
            }
        }

        private fun close(closeable: Closeable?) {
            if (closeable == null) return
            try {
                closeable.close()
            } catch (ignored: IOException) {
            }
        }
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio < 1 && width > maxSize) {

            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else if (height > maxSize) {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}