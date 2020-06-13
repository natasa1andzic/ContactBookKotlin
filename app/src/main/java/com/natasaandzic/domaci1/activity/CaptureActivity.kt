package com.natasaandzic.domaci1.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread

import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.widget.Button
import android.widget.Toast
import com.natasaandzic.domaci1.R

import java.io.*
import java.util.*

class CaptureActivity : AppCompatActivity() {
    private var textureView: TextureView? = null
    private var captureBtn: Button? = null

    companion object {
        //provera orijentacije output slike
        private val ORIENTATIONS: SparseIntArray? = SparseIntArray()
        private const val REQUEST_CAMERA_PERMISSION = 200

        init {
            ORIENTATIONS!!.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }

    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var imageDimension: Size? = null

    //saving a photo as a file
    private var file: File? = null
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private var stateCallback: CameraDevice.StateCallback? = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice!!.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice!!.close()
            cameraDevice = null
        }
    }

    private var textureListener: SurfaceTextureListener? = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        textureView!!.surfaceTextureListener = textureListener
        captureBtn!!.setOnClickListener {
            takePicture()
        }
    }

    private fun takePicture() {
        //Is there a camera on the device
        if (cameraDevice == null) return

        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            //dobijamo vrednosti polja karakteristika za kameru na nasem uredjaju
            val characteristics = manager.getCameraCharacteristics(cameraDevice.getId())

            var jpegSizes: Array<Size?>? = null

            jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG)

            //capture image with custom size
            var width = 640
            var height = 480

            //setujemo vrednosti na konfiguraciju nase kamere
            if (jpegSizes != null && jpegSizes.size > 0) {
                width = jpegSizes[0]!!.width
                height = jpegSizes[0]!!.height
            }

            val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1)

            val outputSurface: MutableList<Surface?> = ArrayList(2)
            outputSurface.add(reader.surface)
            outputSurface.add(Surface(textureView!!.surfaceTexture))

            val captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(reader.surface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)

            //check orientation base on device
            val rotation = windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS!!.get(rotation))

            file = File(Environment.getExternalStorageDirectory().toString() + "/" + UUID.randomUUID().toString() + ".jpg")

            val readerListener: OnImageAvailableListener = object : OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader?) {
                    var image: Image? = null
                    try {
                        image = reader!!.acquireLatestImage()
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.capacity())
                        buffer[bytes]
                        save(bytes)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        run { image?.close() }
                    }
                }

                @Throws(IOException::class)
                private fun save(bytes: ByteArray?) {
                    var outputStream: OutputStream? = null
                    try {
                        outputStream = FileOutputStream(file)
                        outputStream.write(bytes)
                    } finally {
                        outputStream?.close()
                    }
                }
            }
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler)
            val captureListener: CaptureCallback = object : CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest
                                                , result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)
                    Toast.makeText(this@CaptureActivity, "Saved $file", Toast.LENGTH_SHORT).show()
                    createCameraPreview()
                }
            }
            cameraDevice.createCaptureSession(outputSurface, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureListener, mBackgroundHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {}
            }, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun createCameraPreview() {
        try {
            val texture = textureView.getSurfaceTexture()!!
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight())
            val surface = Surface(texture)
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)
            cameraDevice.createCaptureSession(Arrays.asList(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    //ako ne postoji kamera na uredjaju
                    if (cameraDevice == null) return
                    cameraCaptureSessions = cameraCaptureSession
                    updatePreview()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Toast.makeText(this@CaptureActivity, "Changed", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updatePreview() {
        //ako nemamo kameru na uredjaju
        if (cameraDevice == null) Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        //CONTROL_MODE = autoexposure, autowhitebalance, autofocus, CONTROL_MODE_AUTO = auto vrednosti
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        try {
            //request endlessly repeating capture of images by this capture session
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun openCamera() {
        //pravimo instancu CameraManager-a
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            //dobijamo id nase kamere
            cameraId = manager.cameraIdList[0]
            //uzimamo vrednosti polja konfiguracije nase kamere
            val characteristics = manager.getCameraCharacteristics(cameraId)
            //StreamConfigurationMap sluzi kao storage za konfiguraciju
            //SCALER_STREAM_CONFIGURATION_MAP = konfiguracija kamere na nasem uredjaju
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            imageDimension = map.getOutputSizes(SurfaceTexture::class.java)[0]

            //Provera za dozvole
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf<String?>(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), REQUEST_CAMERA_PERMISSION)
                return
            }
            //ako imamo dozvolu, otvorimo kameru
            //metoda CameraManager.openCamera(id, callback, handler)
            manager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        if (textureView.isAvailable()) openCamera() else textureView.setSurfaceTextureListener(textureListener)
    }

    override fun onPause() {
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("Camera background")
        mBackgroundThread.start()
        mBackgroundHandler = Handler(mBackgroundThread.getLooper())
    }

    private fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}