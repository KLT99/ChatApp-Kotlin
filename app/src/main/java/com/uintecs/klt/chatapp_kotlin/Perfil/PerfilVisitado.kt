package com.uintecs.klt.chatapp_kotlin.Perfil

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uintecs.klt.chatapp_kotlin.Model.Usuario
import com.uintecs.klt.chatapp_kotlin.R

class PerfilVisitado : AppCompatActivity() {

    private lateinit var pv_imageUser : ImageView
    private lateinit var pv_nombreU : TextView
    private lateinit var pv_emailU : TextView
    private lateinit var pv_uid : TextView

    private lateinit var pv_nombres : TextView
    private lateinit var pv_apellidos : TextView
    private lateinit var pv_profesion : TextView
    private lateinit var pv_telefono : TextView
    private lateinit var pv_edad : TextView
    private lateinit var pv_domicilio : TextView
    private lateinit var pv_proveedor : TextView

    var uid_usuario_visitado = ""

    private lateinit var btn_llamar : Button
    private lateinit var btn_send_sms : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_visitado)

        inicializarVistas()
        obtenerUid()
        leerInfoUser()

        btn_llamar.setOnClickListener {

            if(ContextCompat.checkSelfPermission(applicationContext,

                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                    RealizarLlamada()

            } else {
                requestCallPhonePermission.launch(Manifest.permission.CALL_PHONE)
            }

        }

        btn_send_sms.setOnClickListener {

            if (ContextCompat.checkSelfPermission(applicationContext,

                    Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                    enviarSms()

            } else {
                requestSendMessagePermission.launch(Manifest.permission.SEND_SMS)
            }

        }

    }

    private fun inicializarVistas() {

        pv_imageUser = findViewById(R.id.pv_ImagenUser)
        pv_nombreU  = findViewById(R.id.pv_nombreU)
        pv_emailU   = findViewById(R.id.pv_emailU)
        pv_uid      = findViewById(R.id.pv_uid)

        pv_nombres      = findViewById(R.id.pv_nombres)
        pv_apellidos    = findViewById(R.id.pv_apellidos)
        pv_profesion    = findViewById(R.id.pv_profesion)
        pv_telefono     = findViewById(R.id.pv_telefono)
        pv_edad         = findViewById(R.id.pv_edad)
        pv_domicilio    = findViewById(R.id.pv_domicilio)
        pv_proveedor    = findViewById(R.id.pv_proveedor)

        btn_llamar      = findViewById(R.id.btn_llamar)
        btn_send_sms    = findViewById(R.id.btn_send_sms)

    }

    private fun obtenerUid() {

        intent = intent
        uid_usuario_visitado = intent.getStringExtra("uid").toString()
    }


    private fun leerInfoUser(){

        val reference = FirebaseDatabase.getInstance().reference
            .child("Usuarios")
            .child(uid_usuario_visitado)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val user : Usuario?= snapshot.getValue(Usuario::class.java)

                pv_nombreU.text = user!!.getN_Usuario()
                pv_emailU.text = user!!.getEmail()
                pv_uid.text = user!!.getUid()

                pv_nombres.text = user!!.getNombres()
                pv_apellidos.text = user!!.getApellidos()
                pv_profesion.text = user!!.getProfesion()
                pv_telefono.text = user!!.getTelefono()
                pv_edad.text = user!!.getEdad()
                pv_domicilio.text = user!!.getDomicilio()
                pv_proveedor.text = user!!.getProveedor()

                Glide.with(applicationContext).load(user.getImagen())
                    .placeholder(R.drawable.imagen_usuario_visitado)
                    .into(pv_imageUser)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun RealizarLlamada() {

        val numUsuario = pv_telefono.text.toString()

        if (numUsuario.isEmpty()){

            Toast.makeText(applicationContext, "El usuario no cuenta con número telefónico", Toast.LENGTH_SHORT).show()

        } else {

            val intent = Intent(Intent.ACTION_CALL)

            intent.setData(Uri.parse("tel:$numUsuario"))
            startActivity(intent)
        }
    }

    private fun enviarSms() {
        val numUsuario = pv_telefono.text.toString()
        if (numUsuario.isEmpty()){

            Toast.makeText(applicationContext, "El usuario no cuenta con número telefónico", Toast.LENGTH_SHORT).show()

        } else {

            val intent = Intent(Intent.ACTION_SENDTO)

            intent.setData(Uri.parse("smsto:$numUsuario"))
            intent.putExtra("sms_body", "")
            startActivity(intent)
        }
    }


    private val requestCallPhonePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){Permiso_concedido->

            if(Permiso_concedido){

                RealizarLlamada()

            } else{

                Toast.makeText(applicationContext, "El permiso de realizar llamadas no ha sido concedido", Toast.LENGTH_SHORT).show()

            }
        }

    private val requestSendMessagePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){Permiso_concedido->

            if(Permiso_concedido){

                enviarSms()

            } else{

                Toast.makeText(applicationContext, "El permiso de enviar SMS no ha sido concedido", Toast.LENGTH_SHORT).show()

            }
        }

}