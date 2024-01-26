package com.example.androidtest

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class CameraAlbumActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_album)
        val imageView = findViewById<ImageView>(R.id.imageView)
//        registerForActivityResult 的调用移动到 onCreate 方法的开始部分，这样可以确保在 LifecycleOwner 的生命周期状态为 STARTED 之前调用 register 方法。
        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    // 在这里处理返回的结果 data为null
//                    val data: Intent? = result.data
//                       将拍摄的照片显示出来
                    val bitmap =
                        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    imageView.setImageBitmap(rotateIfRequired(bitmap))
                }
            }
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                // Handle the returned Uri
                uri?.let {
                    // 从 Uri 读取图片
                    val inputStream = contentResolver.openInputStream(it)
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)

                    // 创建输出文件
                    val outputImage = File(externalCacheDir, "output_image.jpg")
                    if (outputImage.exists()) {
                        outputImage.delete()
                    }
                    outputImage.createNewFile()
                    val outputStream = FileOutputStream(outputImage)

                    // 压缩图片并保存到文件中
                    val compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.width / 6, originalBitmap.height / 6, true)
                    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

                    // 关闭输出流
                    outputStream.close()

                    // 将压缩后的图片显示在 ImageView 中
                    imageView.setImageBitmap(compressedBitmap)
                }
            }
        val takePhotoBtn = findViewById<Button>(R.id.takePhotoBtn)
        takePhotoBtn.setOnClickListener {
//            创建File对象，用于存储拍照后的图片（在应用关联缓存目录下）
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()

//            从Android 7.0系统开始，直接使用本地真实路径的Uri被认为是不安全的
            imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                FileProvider是一种特殊的ContentProvider，使用了和ContentProvider类似的机制对数据进行保护，可以选择性地将封装过的Uri共享给外部，从而提高了应用的安全性。
                FileProvider.getUriForFile(
                    this, "com.example.androidtest.fileprovider", outputImage
                )
            } else {
                Uri.fromFile(outputImage)
            }
//            启动相机程序
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
// 启动 Activity
            startForResult.launch(intent)
        }
        val fromAlbumBtn = findViewById<Button>(R.id.fromAlbumBtn)
        fromAlbumBtn.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }

    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matirx = Matrix()
        matirx.postRotate(degree.toFloat())
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matirx, true)
//        将不再需要的Bitmap对象回收
        bitmap.recycle()
        return rotatedBitmap
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }
}