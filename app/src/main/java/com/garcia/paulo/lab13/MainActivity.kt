package com.garcia.paulo.lab13

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.garcia.paulo.lab13.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    // Declaración de las variables necesarias para el binding y la cámara
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imageCaptureExecutor: ExecutorService

    // Solicitud de permiso para la cámara
    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera() // Si el permiso es otorgado, inicia la cámara
            } else {
                // Si no se otorga el permiso, muestra un mensaje
                Snackbar.make(
                    binding.root,
                    "The camera permission is necessary",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicialización de CameraProvider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        // Selección de cámara por defecto (trasera)
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        // Executor para la captura de imágenes
        imageCaptureExecutor = Executors.newSingleThreadExecutor()

        // Solicitar el permiso para usar la cámara
        cameraPermissionResult.launch(Manifest.permission.CAMERA)

        // Configuración del botón de captura de imagen
        binding.imgCaptureBtn.setOnClickListener {
            takePhoto()  // Llamar a la función que captura la foto
        }

        // Configuración del botón para cambiar entre cámaras
        binding.switchBtn.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA // Cambiar a la cámara frontal
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA // Volver a la cámara trasera
            }
            startCamera()  // Reiniciar la cámara con la nueva selección
        }

        // Configuración del botón de galería
        binding.galleryBtn.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)  // Intent para abrir la galería
            startActivity(intent)
        }
    }

    // Inicia la cámara
    private fun startCamera() {
        // Crear un objeto Preview para mostrar la vista previa de la cámara
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.preview.surfaceProvider)
        }

        // Obtener el CameraProvider
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Crear el objeto ImageCapture para capturar imágenes
            imageCapture = ImageCapture.Builder().build()

            try {
                // Desvincular todos los casos de uso anteriores (en caso de que haya algo vinculado)
                cameraProvider.unbindAll()

                // Vincular la cámara con el ciclo de vida de la actividad
                cameraProvider.bindToLifecycle(
                    this, // El ciclo de vida de la actividad
                    cameraSelector, // El selector de cámara (trasera o frontal)
                    preview, // La vista previa de la cámara
                    imageCapture // El caso de uso de captura de imagen
                )

            } catch (e: Exception) {
                // Si algo sale mal al vincular los casos de uso
                Log.d(TAG, "Use case binding failed: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this)) // Ejecuta en el hilo principal
    }


    // Función para capturar una foto
    private fun takePhoto() {
        // Verifica si 'imageCapture' no es nulo antes de continuar
        imageCapture?.let { it: ImageCapture ->

            // Crea un nombre único para la imagen usando la marca de tiempo actual
            val fileName = "JPEG_${System.currentTimeMillis()}"

            // Crea el archivo donde se guardará la imagen, utilizando el directorio de medios externos
            val file = File(externalMediaDirs[0], fileName)

            // Configura las opciones del archivo de salida para la captura de imagen
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            // Toma la foto y la guarda en el archivo especificado
            it.takePicture(
                outputFileOptions,
                imageCaptureExecutor,  // Ejecuta la operación en un hilo en segundo plano
                object : ImageCapture.OnImageSavedCallback {
                    // Se ejecuta cuando la foto se guarda correctamente
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i(TAG, "The image has been saved in ${file.toUri()}")  // Imprime la ruta del archivo guardado
                    }

                    // Se ejecuta si ocurre un error al tomar la foto
                    override fun onError(exception: ImageCaptureException) {
                        // Muestra un mensaje de error al usuario
                        Toast.makeText(
                            binding.root.context,
                            "Error taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                        // Registra el error en el log
                        Log.d(TAG, "Error taking photo: $exception")
                    }
                }
            )
        }
    }


    companion object {
        const val TAG = "MainActivity" // Definir un TAG constante para los logs
    }
}
