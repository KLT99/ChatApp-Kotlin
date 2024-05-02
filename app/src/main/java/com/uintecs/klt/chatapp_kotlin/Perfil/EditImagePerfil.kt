package com.uintecs.klt.chatapp_kotlin.Perfil

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.uintecs.klt.chatapp_kotlin.R

class EditImagePerfil : AppCompatActivity() {

    private lateinit var imgPerfilUpdate : ImageView

    private lateinit var btnSelectImg : Button
    private lateinit var btnUpdateImg : Button
    private var imgUri : Uri?= null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image_perfil)

        imgPerfilUpdate = findViewById(R.id.imgPerfilUpdate)

        btnSelectImg = findViewById(R.id.btnSelectImg)
        btnUpdateImg = findViewById(R.id.btnUpdateImg)

        progressDialog = ProgressDialog(this@EditImagePerfil)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        btnSelectImg.setOnClickListener {
            //Toast.makeText(applicationContext, "Seleccionar imagen", Toast.LENGTH_SHORT).show()
            show_dialog()
        }

        btnUpdateImg.setOnClickListener {

            //Toast.makeText(applicationContext, "Actualizar imagen", Toast.LENGTH_SHORT).show()
            validarImg()

        }

    }

    private fun validarImg() {

        if(imgUri == null){

            Toast.makeText(applicationContext, "Es necesario una imagen", Toast.LENGTH_SHORT).show()

        } else {
            upImage()
        }
    }

    private fun upImage() {
        progressDialog.setMessage("Actualizando imagen...")
        progressDialog.show()

        val uriImg = "Perfil_user/"+firebaseAuth.uid
        val referenceStorage = FirebaseStorage.getInstance().getReference(uriImg)
        referenceStorage.putFile(imgUri!!).addOnSuccessListener {task->

            //obtener url de la imagen
            val uriTask : Task<Uri> = task.storage.downloadUrl

            while (!uriTask.isSuccessful);
            val urlImg = "${uriTask.result}"
            updateImgDB(urlImg)

        }.addOnFailureListener {e->

            Toast.makeText(applicationContext, "No se ha podido subir la imagen debido a un error: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateImgDB(urlImg: String) {

        progressDialog.setMessage("Actualizando imagen de perfil")

        val hashmap : HashMap<String, Any> = HashMap()

        if(imgUri!=null){

            hashmap["imagen"] = urlImg
        }

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(firebaseAuth.uid!!).updateChildren(hashmap).addOnSuccessListener {

            progressDialog.dismiss()

            Toast.makeText(applicationContext, "Su imagen a sido actualizada", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{e->

            Toast.makeText(applicationContext, "No se ha actualizado su imagen debido a error: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun show_dialog() {

        val btn_openGalery : Button
        val btn_openCamera : Button

        val dialog = Dialog(this@EditImagePerfil)

        dialog.setContentView(R.layout.dialog_select)

        btn_openGalery = dialog.findViewById(R.id.btnGalery)
        btn_openCamera = dialog.findViewById(R.id.btnCamera)

        btn_openGalery.setOnClickListener {
            //Toast.makeText(applicationContext, "Abrir Galería", Toast.LENGTH_SHORT).show()
            openGalery()

            dialog.dismiss()
        }

        btn_openCamera.setOnClickListener {
            //Toast.makeText(applicationContext, "Abrir Camara", Toast.LENGTH_SHORT).show()
            openCamera()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openGalery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        galeryActivityResultLauncher.launch(intent)

    }

    private val galeryActivityResultLauncher = registerForActivityResult(

        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{resultado->

            if(resultado.resultCode == RESULT_OK){
                val data = resultado.data
                imgUri = data!!.data

                imgPerfilUpdate.setImageURI(imgUri)

            } else{

                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()

            }
        }
    )

    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion")

        imgUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)

        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result_camera->

                if(result_camera.resultCode == RESULT_OK){

                    imgPerfilUpdate.setImageURI(imgUri)

                } else {

                    Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()

                }
        }

}