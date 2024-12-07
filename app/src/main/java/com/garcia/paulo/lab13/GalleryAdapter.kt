package com.garcia.paulo.lab13

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garcia.paulo.lab13.databinding.ListItemImgBinding
import java.io.File

// Adaptador para mostrar imágenes en un RecyclerView
class GalleryAdapter(private val fileArray: Array<File>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    // ViewHolder para cada elemento de la lista
    class ViewHolder(private val binding: ListItemImgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Función para vincular el archivo a la vista
        fun bind(file: File) {
            // Usando Glide para cargar la imagen en el ImageView
            Glide.with(binding.root).load(file).into(binding.localImg)
        }
    }

    // Crea una nueva vista para cada elemento en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)  // Inflar el layout para cada ítem
        // Corregir: El segundo parámetro es el parent y el tercer parámetro es false para no adjuntar inmediatamente.
        return ViewHolder(ListItemImgBinding.inflate(layoutInflater, parent, false))
    }

    // Vincula los datos (imagen) al ViewHolder en la posición correspondiente
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileArray[position])  // Vincula el archivo a la vista del ViewHolder
    }

    // Devuelve el número total de elementos en el array de archivos
    override fun getItemCount(): Int {
        return fileArray.size  // Número de elementos en el array
    }
}
