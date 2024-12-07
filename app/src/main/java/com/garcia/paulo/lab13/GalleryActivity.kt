package com.garcia.paulo.lab13

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.garcia.paulo.lab13.databinding.ActivityGalleryBinding
import java.io.File

class GalleryActivity : AppCompatActivity() {
    // Declaración del objeto de binding para acceder a las vistas de la actividad
    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de binding inflando el layout de la actividad
        binding = ActivityGalleryBinding.inflate(layoutInflater)

        // Establece el contenido de la actividad a la raíz del binding
        setContentView(binding.root)

        // Obtiene el directorio de medios externos (almacenamiento accesible de la app)
        val directory = File(externalMediaDirs[0].absolutePath)

        // Obtiene los archivos del directorio como un arreglo de archivos
        val files = directory.listFiles() as Array<File>

        // Crea el adaptador pasando los archivos, invirtiendo el orden del arreglo
        val adapter = GalleryAdapter(files.reversedArray())

        // Establece el adaptador al ViewPager para mostrar las imágenes
        binding.viewPager.adapter = adapter
    }
}