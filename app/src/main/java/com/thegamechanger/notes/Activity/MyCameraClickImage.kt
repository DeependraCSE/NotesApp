package com.thegamechanger.notes.Activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import com.thegamechanger.notes.Helper.AppConstant
import com.thegamechanger.notes.R
import com.yalantis.ucrop.UCrop
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MyCameraClickImage : AppCompatActivity(),SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener {
    internal var holder: SurfaceHolder?=null
    internal var camera: Camera?=null
    internal var mPicture: Camera.PictureCallback?=null
    var mediaStorageDir: File?=null
    internal var alteredBitmap: Bitmap? = null
    internal var capture_bitmap:Bitmap?=null
    internal var canvas: Canvas?=null
    internal var paint: Paint?=null
    internal var capture: ImageView?=null
    internal var rotate_camera:ImageView?=null
    internal var camera_face: Int = 0
    internal var mDist = 0f
    internal var isDraw: Boolean = false
    internal var isEdit:Boolean = false
    internal var matrix: Matrix?=null
    internal var sourceImageUri: Uri?=null
    internal var cropImageUri: Uri?=null
    //dialog view
    internal var dialog: Dialog?=null
    internal var tv_clear: TextView?=null
    internal var captured_image: ImageView?=null
    internal var iv_crop:ImageView?=null
    internal var iv_anticlock_rotate:ImageView?=null
    internal var iv_clock_rotate:ImageView?=null
    internal var edit_capture:ImageView?=null
    internal var cancel_capture:ImageView?=null
    internal var ok_capture:ImageView?=null

    var downx = 0f
    var downy = 0f
    var upx = 0f
    var upy = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mycameraclickimage)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val cameraView = findViewById(R.id.CameraView) as SurfaceView
        holder = cameraView.holder
        holder!!.addCallback(this)

        mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), AppConstant.MEDIA_FOLDER)

        mPicture = Camera.PictureCallback { bytes, camera ->
            val options = BitmapFactory.Options()
            options.inMutable = true
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            matrix = Matrix()

            if (camera_face == AppConstant.CAMERA_FACE_FRONT) {
                val mirrorY = floatArrayOf(-1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
                val matrixMirrorY = Matrix()
                matrixMirrorY.setValues(mirrorY)
                matrix!!.postConcat(matrixMirrorY)
            }
            matrix!!.postRotate(90f)


            val scaledBitmap = Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, true)

            capture_bitmap = Bitmap.createBitmap(
                scaledBitmap,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )

            //capture_bitmap = GetRoundedRectBitmap(capture_bitmap);

            alteredBitmap = Bitmap.createBitmap(
                capture_bitmap!!.getWidth(),
                capture_bitmap!!.getHeight(),
                capture_bitmap!!.getConfig()
            )
            ShowDialogPicture(capture_bitmap!!)
        }

        val bundle = intent.extras
        if (intent.hasExtra(AppConstant.CAMERA_FACE)) {
            camera_face = bundle!!.getInt(AppConstant.CAMERA_FACE)
        } else {
            camera_face = AppConstant.CAMERA_FACE_REAR
        }


        capture = findViewById(R.id.capture) as ImageView
        capture!!.setOnClickListener(this)

        rotate_camera = findViewById(R.id.rotate_camera) as ImageView
        rotate_camera!!.setOnClickListener(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        RefreshCamera()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        camera!!.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        CreateSerface()
    }

    override fun onClick(v: View?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (v!!.getId()) {
            R.id.rotate_camera -> ChangeCamera()
            R.id.capture -> CapturePicture()
            R.id.iv_crop -> CropImage()
            R.id.iv_anticlock_rotate -> RotateImage(-90f)
            R.id.iv_clock_rotate -> RotateImage(90f)
            R.id.edit_capture -> EditImage()
            R.id.clear -> ClearImage()
            R.id.cancel_capture -> CancelImage()
            R.id.ok_capture -> SaveImage()
        }
    }

    fun RefreshCamera() {
        if (holder!!.getSurface() == null) {
            return
        }
        try {
            camera!!.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            camera!!.setPreviewDisplay(holder)
            camera!!.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getOutputMediaFile(type:Int):File? {
    if (!mediaStorageDir!!.exists()){
        if (!mediaStorageDir!!.mkdirs()) {
            Log.d("MyCameraApp", "failed to create directory")
            return null
        }
    }

     // Create a media mFile name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val mediaFile:File
    if (type == AppConstant.MEDIA_TYPE_IMAGE){
        mediaFile = File(mediaStorageDir!!.getPath() + File.separator + "IMG_" + timeStamp + ".png")
    }else if (type == AppConstant.MEDIA_TYPE_VIDEO){
        mediaFile = File((mediaStorageDir!!.getPath() + File.separator + "VID_" + timeStamp + ".mp4"))
        Log.e("VideoPath", Uri.fromFile(mediaFile).toString())
    }else{
        return null
    }

    return mediaFile
}

    private fun CapturePicture() {
        camera!!.takePicture(null, null, mPicture)
    }

    private fun CreateSerface() {

        try {
            if (camera_face == AppConstant.CAMERA_FACE_FRONT) {
                camera = Camera.open(1) //open a front camera
            } else {
                camera = Camera.open() //open a camera
            }

        } catch (e: Exception) {
            Log.i("Exception", e.toString())
            return
        }

        val param = camera!!.getParameters()
        param.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        //param.zoom = param.maxZoom / 2
        param.supportedPictureSizes


        /*param.setPictureSize(640, 480)
        param.setPreviewSize(640, 480)*/
        param.setPictureSize(1280, 960)
        param.setPreviewSize(1280, 960)


        /*for (size in param.supportedPictureSizes) {
            Log.e("Picture Size","Width:"+size.width+" Height:"+size.height);
        }*/

        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

        if (display.rotation == Surface.ROTATION_0) {
            camera!!.setDisplayOrientation(90)
        }

        try {
            camera!!.setParameters(param)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            camera!!.setPreviewDisplay(holder)
            camera!!.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }

    fun ChangeCamera() {
        camera!!.release()
        if (camera_face == AppConstant.CAMERA_FACE_FRONT) {
            camera_face = AppConstant.CAMERA_FACE_REAR
        } else if (camera_face == AppConstant.CAMERA_FACE_REAR) {
            camera_face = AppConstant.CAMERA_FACE_FRONT
        }
        CreateSerface()
    }

    private fun ShowDialogPicture(bitmap: Bitmap) {
        isDraw = false
        isEdit = false

        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dialog_captured_image)
        dialog!!.getWindow()!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
        dialog!!.setCanceledOnTouchOutside(false)

        captured_image = dialog!!.findViewById(R.id.captured_image) as ImageView
        captured_image!!.setOnTouchListener(this)

        iv_crop = dialog!!.findViewById(R.id.iv_crop) as ImageView
        iv_crop!!.setOnClickListener(this)

        iv_anticlock_rotate = dialog!!.findViewById(R.id.iv_anticlock_rotate) as ImageView
        iv_anticlock_rotate!!.setOnClickListener(this)

        iv_clock_rotate = dialog!!.findViewById(R.id.iv_clock_rotate) as ImageView
        iv_clock_rotate!!.setOnClickListener(this)

        edit_capture = dialog!!.findViewById(R.id.edit_capture) as ImageView
        edit_capture!!.setOnClickListener(this)

        tv_clear = dialog!!.findViewById(R.id.clear) as TextView
        tv_clear!!.setOnClickListener(this)

        cancel_capture = dialog!!.findViewById(R.id.cancel_capture) as ImageView
        cancel_capture!!.setOnClickListener(this)

        ok_capture = dialog!!.findViewById(R.id.ok_capture) as ImageView
        ok_capture!!.setOnClickListener(this)

        DrawableCompat.setTint(edit_capture!!.getDrawable(), Color.WHITE)
        DrawableCompat.setTint(cancel_capture!!.getDrawable(), Color.WHITE)

        CreateImageForSketch(bitmap)
        dialog!!.show()
    }

    fun CreateImageForSketch(bitmap: Bitmap) {
        canvas = Canvas(alteredBitmap)

        paint = Paint()
        paint!!.setColor(Color.RED)
        paint!!.setStrokeWidth(5f)
        matrix = Matrix()
        canvas!!.drawBitmap(bitmap, matrix, paint)
        captured_image!!.setImageBitmap(alteredBitmap)
    }

    fun CropImage() {
        if (alteredBitmap != null) {

            val s_imageFile = getOutputMediaFile(AppConstant.MEDIA_TYPE_IMAGE)
            sourceImageUri = Uri.fromFile(s_imageFile)
            val s_imageFileOS = contentResolver.openOutputStream(sourceImageUri)
            alteredBitmap!!.compress(Bitmap.CompressFormat.JPEG, 99, s_imageFileOS)

            val c_imageFile = getOutputMediaFile(AppConstant.MEDIA_TYPE_IMAGE)
            cropImageUri = Uri.fromFile(c_imageFile)
            val c_imageFileOS = contentResolver.openOutputStream(cropImageUri)
            alteredBitmap!!.compress(Bitmap.CompressFormat.JPEG, 99, c_imageFileOS)
            UCrop.of(sourceImageUri!!, cropImageUri!!).withAspectRatio(3f, 4f).withMaxResultSize(alteredBitmap!!.getWidth(), alteredBitmap!!.getHeight()).start(this@MyCameraClickImage);

        }
    }

    fun RotateImage(degree: Float) {
        val bMap = alteredBitmap
        val mat = Matrix()
        mat.postRotate(degree)
        val bMapRotate =
            Bitmap.createBitmap(bMap, 0, 0, bMap!!.getWidth(), bMap!!.getHeight(), mat, true)
        alteredBitmap = bMapRotate
        CreateImageForSketch(alteredBitmap!!)
    }

    fun EditImage() {
        if (!isEdit) {
            isDraw = true
            DrawableCompat.setTint(edit_capture!!.getDrawable(), Color.RED)
            isEdit = true
        } else {
            isDraw = false
            DrawableCompat.setTint(edit_capture!!.getDrawable(), Color.WHITE)
            isEdit = false
        }
    }

    fun ClearImage() {
        alteredBitmap = Bitmap.createBitmap(
            capture_bitmap!!.getWidth(),
            capture_bitmap!!.getHeight(),
            capture_bitmap!!.getConfig()
        )
        CreateImageForSketch(capture_bitmap!!)
    }

    fun CancelImage() {
        dialog!!.dismiss()
        camera!!.startPreview()
    }

    fun SaveImage() {
        //WriteText(alteredBitmap!!)
        if (alteredBitmap != null) {
            //alteredBitmap = GetRoundedRectBitmap(alteredBitmap);
            val imageFile = getOutputMediaFile(AppConstant.MEDIA_TYPE_IMAGE)
            val imageFileUri = Uri.fromFile(imageFile)
            try {
                val imageFileOS = contentResolver.openOutputStream(imageFileUri)
                alteredBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, imageFileOS)
                dialog!!.dismiss()

                var f = compressImage(imageFile!!)

                val intent = Intent()
                //intent.putExtra(AppConstant.MEDIA_PATH, imageFile!!.path)
                intent.putExtra(AppConstant.MEDIA_PATH, f)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } catch (e: Exception) {
                Log.v("EXCEPTION", e.message)
            }

        }
    }

    fun WriteText(bitmap: Bitmap) {
        val pPint = Paint()
        pPint.color = Color.WHITE
        canvas!!.drawRect(
            0f,
            bitmap.height.toFloat(),
            bitmap.width.toFloat(),
            (bitmap.height - 50).toFloat(),
            pPint
        )
        pPint.color = Color.BLACK
        pPint.textSize = 20f
        canvas!!.drawText("Sample Text", 10f, (bitmap.height - 10).toFloat(), pPint)

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val action = event!!.getAction()
        val x = event.getX().toInt()
        val y = event.getY().toInt()

        when (action) {
            MotionEvent.ACTION_DOWN -> if (isDraw) {
                if (x < 0 || y < 0 || x > captured_image!!.getWidth() || y > captured_image!!.getHeight()) {
                    //outside ImageView
                } else {
                    downx =(x.toDouble() * (alteredBitmap!!.getWidth().toDouble() / captured_image!!.getWidth().toDouble())).toInt().toFloat()
                    downy =(y.toDouble() * (alteredBitmap!!.getHeight().toDouble() / captured_image!!.getHeight().toDouble())).toInt().toFloat()
                    captured_image!!.invalidate()
                }

            }
            MotionEvent.ACTION_MOVE -> if (isDraw) {
                if (x < 0 || y < 0 || x > captured_image!!.getWidth() || y > captured_image!!.getHeight()) {
                    //outside ImageView
                } else {
                    upx =(x.toDouble() * (alteredBitmap!!.getWidth().toDouble() / captured_image!!.getWidth().toDouble())).toInt().toFloat()
                    upy =(y.toDouble() * (alteredBitmap!!.getHeight().toDouble() / captured_image!!.getHeight().toDouble())).toInt().toFloat()
                    canvas!!.drawLine(downx, downy, upx, upy, paint)
                    captured_image!!.invalidate()
                    downx = upx
                    downy = upy
                }
            }
            MotionEvent.ACTION_UP -> if (isDraw) {
                if (x < 0 || y < 0 || x > captured_image!!.getWidth() || y > captured_image!!.getHeight()) {
                    //outside ImageView
                } else {
                    upx = (x.toDouble() * (alteredBitmap!!.getWidth().toDouble() / captured_image!!.getWidth().toDouble())).toInt().toFloat()
                    upy =(y.toDouble() * (alteredBitmap!!.getHeight().toDouble() / captured_image!!.getHeight().toDouble())).toInt().toFloat()
                    canvas!!.drawLine(downx, downy, upx, upy, paint)
                    captured_image!!.invalidate()
                }

            }
            MotionEvent.ACTION_CANCEL -> {
            }
            else -> {
            }
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Get the pointer ID
        val params = camera!!.getParameters()
        val action = event.action


        if (event.pointerCount > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event)
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported) {
                camera!!.cancelAutoFocus()
                handleZoom(event, params)
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params)
            }
        }
        return true
    }

    private fun handleZoom(event: MotionEvent, params: Camera.Parameters) {
        val maxZoom = params.maxZoom
        var zoom = params.zoom
        val newDist = getFingerSpacing(event)
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--
        }
        mDist = newDist
        params.zoom = zoom
        camera!!.setParameters(params)
    }

    fun handleFocus(event: MotionEvent, params: Camera.Parameters) {
        val pointerId = event.getPointerId(0)
        val pointerIndex = event.findPointerIndex(pointerId)
        // Get the pointer's current position
        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)

        val supportedFocusModes = params.supportedFocusModes
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            camera!!.autoFocus(Camera.AutoFocusCallback { b, camera ->
                // currently set to auto-focus on single touch
            })
        }
    }

    private fun getFingerSpacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uri: Uri?=null
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            uri = UCrop.getOutput(data!!)

            val bMap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            val workingBitmap = Bitmap.createBitmap(bMap)
            alteredBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
            CreateImageForSketch(alteredBitmap!!)
        }
        if (null != uri){
            val udelete = File(uri!!.getPath()!!)
            if (udelete.exists()) {
                udelete.delete()
            }
        }
        val sdelete = File(sourceImageUri!!.getPath()!!)
        if (sdelete.exists()) {
            sdelete.delete()
        }
        val cdelete = File(cropImageUri!!.getPath()!!)
        if (cdelete.exists()) {
            cdelete.delete()
        }
    }

    fun GetRoundedRectBitmap(bitmap: Bitmap): Bitmap? {
        var bitmap = bitmap
        var result: Bitmap? = null

        val bWidth = bitmap.width
        val bHeigth = bitmap.height

        val size = if (bWidth < bHeigth) bWidth else bHeigth



        try {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result!!)

            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, size, size)

            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawCircle(
                (size / 2).toFloat(),
                (size / 2).toFloat(),
                (size / 2).toFloat(),
                paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
            canvas.drawBitmap(bitmap, rect, rect, paint)

        } catch (e: NullPointerException) {
        } catch (o: OutOfMemoryError) {
        }

        return result
    }

    fun DpToPixel(dp: Int): Int {
        return dp * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun PixelsToDp(px: Int): Int {
        return px / (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun compressImage(file : File) : String{

        var filePath = file.absolutePath;
        var scaledBitmap : Bitmap?=null

        var options = BitmapFactory.Options()

        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)

        var actualHeight = options.outHeight;
        var actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        var maxHeight = 816.0f
        var maxWidth = 612.0f
        var imgRatio : Float = (actualWidth.toFloat() / actualHeight.toFloat())
        var maxRatio = (maxWidth / maxHeight)

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight/actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight =  maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth =  maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = ByteArray(16 * 1024)

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (exception : OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (exception : OutOfMemoryError) {
            exception.printStackTrace()
        }

        var ratioX = actualWidth / ( options.outWidth.toFloat())
        var ratioY = actualHeight / (options.outHeight.toFloat())
        var middleX = actualWidth / 2.0f
        var middleY = actualHeight / 2.0f

        var scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        var canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, Paint(Paint.FILTER_BITMAP_FLAG))


        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap!!.getWidth(), scaledBitmap!!.getHeight(), matrix, true)

        try {
            var out = FileOutputStream(file)
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)

        } catch (e : FileNotFoundException) {
            e.printStackTrace()
        }
        return file!!.path
    }

    fun calculateInSampleSize(options : BitmapFactory.Options, reqWidth : Int, reqHeight : Int) : Int{
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat()/ reqWidth.toFloat() )
            inSampleSize = if(heightRatio < widthRatio)  heightRatio else widthRatio
        }
        val totalPixels = width * height;
        val totalReqPixelsCap = reqWidth * reqHeight * 2
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize
    }
}