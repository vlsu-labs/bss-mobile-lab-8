package com.example.bss_mobile_lab_8


import android.graphics.Matrix
import android.graphics.RectF
import android.hardware.Camera

import android.hardware.Camera.CameraInfo
import android.hardware.Camera.Size
import android.os.Bundle
import android.view.Display
import android.view.Surface
import android.view.SurfaceHolder

import android.view.SurfaceView
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bss_mobile_lab_8.ui.theme.Bssmobilelab8Theme
import java.io.IOException

class MainActivity : ComponentActivity() {

    var sv: SurfaceView? = null
    var sh: SurfaceHolder? = null
    var holder: HolderCallback? = null
    var camera: Camera? = null

    val cameraId: Int = 1
    val isFullScreen: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.view_layout)

        sv = findViewById(R.id.surfaceView2)
        sh = sv!!.holder

        sh!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        var callBack: HolderCallback = HolderCallback(this)
        sh!!.addCallback(callBack)
    }

    override fun onResume() {
        super.onResume()
        camera = Camera.open(cameraId)
        setPreviewSize(isFullScreen)
    }

    private fun setPreviewSize(isFullScreen: Boolean) {
        var display: Display = windowManager.defaultDisplay
        var widthIsMax = display.width > display.height

        var size: Size = camera?.parameters?.previewSize!!

        var rectDisplay: RectF = RectF()
        var rectPreview: RectF = RectF()

        rectDisplay.set(0F, 0F, display.width.toFloat(), display.height.toFloat())

        if (widthIsMax) {
            rectPreview.set(0F, 0F, size.width.toFloat(), size.height.toFloat())
        } else {
            rectPreview.set(0F, 0F, size.height.toFloat(), size.width.toFloat())
        }

        var matrix: Matrix = Matrix()

        if (!isFullScreen) {
            matrix.setRectToRect(rectPreview, rectDisplay, Matrix.ScaleToFit.START)
        } else {
            matrix.setRectToRect(rectDisplay, rectPreview, Matrix.ScaleToFit.START)
            matrix.invert(matrix)
        }

        matrix.mapRect(rectPreview)

        sv!!.layoutParams.height = rectPreview.bottom.toInt()
        sv!!.layoutParams.width = rectPreview.top.toInt()
    }

    fun setCameraDisplayOrierantion(cameraId: Int) {
        var rotaion = windowManager.defaultDisplay.rotation

        var degres = 0

        when (rotaion) {
            Surface.ROTATION_0 -> degres = 0
            Surface.ROTATION_90 -> degres = 90
            Surface.ROTATION_180 -> degres = 180
            Surface.ROTATION_270 -> degres = 270
        }

        var result = 0

        var info: CameraInfo = CameraInfo()
        Camera.getCameraInfo(cameraId, info)

        if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degres) + info.orientation)
        } else if (info.facing == CameraInfo.CAMERA_FACING_FRONT){
            result = ((360 - degres) - info.orientation)
            result += 360
        }

        result %= 360
        camera?.setDisplayOrientation(result)
    }

    class HolderCallback(private val activity: MainActivity): SurfaceHolder.Callback {
        override fun surfaceCreated(p0: SurfaceHolder) {
            try {
                activity.camera!!.setPreviewDisplay(p0)
                activity.camera!!.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            activity.camera!!.stopPreview()
            activity.setCameraDisplayOrierantion(activity.cameraId)

            try {
                activity.camera!!.setPreviewDisplay(p0)
                activity.camera!!.startPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {

        }
    }


}
