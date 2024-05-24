package com.uintecs.klt.chatapp_kotlin.Perfil

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hbb20.CountryCodePicker
import com.uintecs.klt.chatapp_kotlin.Model.Usuario
import com.uintecs.klt.chatapp_kotlin.R

class PerfilActivity : AppCompatActivity() {

    private lateinit var p_image : ImageView
    private lateinit var p_user_name : TextView
    private lateinit var p_email : TextView
    private lateinit var p_proveedor : TextView
    private lateinit var p_name : EditText
    private lateinit var p_lastname : EditText
    private lateinit var p_profession : EditText
    private lateinit var p_address : EditText
    private lateinit var p_age : EditText
    private lateinit var p_telephon : TextView
    private lateinit var btn_save : Button


    private lateinit var btn_verificar : MaterialButton

    private lateinit var edit_image : ImageView
    private lateinit var edt_telefono : ImageView

    var user : FirebaseUser?=null
    var referece: DatabaseReference?=null

    private var codigoTel = ""
    private var numeroTel = ""
    private var codigo_num_tel = ""

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        inicializarVariables()
        obtenerDatos()
        estadoCuenta()

        btn_save.setOnClickListener {

            actualizarInfo()

        }

        edit_image.setOnClickListener {
            val intent = Intent(applicationContext, EditImagePerfil::class.java)
            startActivity(intent)
        }

        edt_telefono.setOnClickListener {

            establecerNumTel()

        }

        btn_verificar.setOnClickListener {

            if (user!!.isEmailVerified){

                //Usuario esta verificado
                //Toast.makeText(applicationContext, "Usuario verificado", Toast.LENGTH_SHORT).show()
                CuentaVerificada()

            } else{
                //Usuario no verificado
                Toast.makeText(applicationContext, "Usuario no verificado", Toast.LENGTH_SHORT).show()

                confirmarEnvio()

            }

        }

    }

    private fun confirmarEnvio() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verificar cuent")
            .setMessage("¿Estás seguro(a) de enviar instrucciones de verificación a su correo electrónico? ${user!!.email}")
            .setPositiveButton("Enviar"){d,e->

                enviarEmailConfirmacion()
            }
            .setNegativeButton("Cancelar"){d,e->

                d.dismiss()
            }
            .show()

    }

    private fun enviarEmailConfirmacion() {

        progressDialog.setMessage("Enviando instrucciones de verificación a su correo electrónico ${user!!.email}")
        progressDialog.show()

        user!!.sendEmailVerification()
            .addOnSuccessListener {
            //Envio fue exitoso
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Instrucciones enviadas, revise la bandeja de su correo ${user!!.email}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                //Envio no fue exitoso
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La operación falló debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun estadoCuenta(){

        if (user!!.isEmailVerified){

            btn_verificar.text = "Verificado"
        } else{
            btn_verificar.text = "No Verificado"

        }

    }

    private fun establecerNumTel() {

        //Declarar vistas del cuadro de dialogo
        val establecer_telefono : EditText
        val selectCodigoPais : CountryCodePicker
        val btn_aceptar : MaterialButton

        val dialog = Dialog(this@PerfilActivity)
        //realizar conexión con el diseño
        dialog.setContentView(R.layout.cuadro_d_establecer_telefono)
        //Inicializar las vistas
        establecer_telefono = dialog.findViewById(R.id.establecer_telefono)
        selectCodigoPais = dialog.findViewById(R.id.selectorCogPais)
        btn_aceptar = dialog.findViewById(R.id.Btn_aceptar_telefono)

        //Asignar un evento al botón
        btn_aceptar.setOnClickListener {

            codigoTel = selectCodigoPais.selectedCountryCodeWithPlus
            numeroTel = establecer_telefono.text.toString().trim()
            codigo_num_tel = codigoTel + numeroTel

            if(numeroTel.isEmpty()){

                Toast.makeText(applicationContext, "Ingrese un número telefónico", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                p_telephon.text = codigo_num_tel
                dialog.dismiss()
            }
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun inicializarVariables() {

        p_image = findViewById(R.id.p_image)
        p_user_name = findViewById(R.id.p_u_name)
        p_email = findViewById(R.id.p_u_email)
        p_proveedor = findViewById(R.id.p_proveedor)
        p_name = findViewById(R.id.p_name)
        p_lastname = findViewById(R.id.p_lastname)
        p_profession = findViewById(R.id.p_profession)
        p_address = findViewById(R.id.p_address)
        p_age = findViewById(R.id.p_age)
        p_telephon = findViewById(R.id.p_telephonenumber)
        btn_save = findViewById(R.id.btn_save)

        edit_image = findViewById(R.id.ed_image)

        edt_telefono = findViewById(R.id.edt_telefono)

        btn_verificar = findViewById(R.id.btn_verificar)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        user = FirebaseAuth.getInstance().currentUser
        referece = FirebaseDatabase.getInstance().reference.child("Usuarios").child(user!!.uid)

    }

    private fun obtenerDatos(){
        referece!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    //Obtenemos datos de Firebase
                    val usuario : Usuario?= snapshot.getValue(Usuario::class.java)
                    val str_user_name = usuario!!.getN_Usuario()
                    val str_email = usuario.getEmail()
                    val str_proveedor = usuario.getProveedor()
                    val str_name = usuario.getNombres()
                    val str_lastname = usuario.getApellidos()
                    val str_profession = usuario.getProfesion()
                    val str_address = usuario.getDomicilio()
                    val str_age = usuario.getEdad()
                    val str_telephon = usuario.getTelefono()

                    //Seteamos la información en las vistas
                    p_user_name.text = str_user_name
                    p_email.text = str_email
                    p_proveedor.text = str_proveedor
                    p_name.setText(str_name)
                    p_lastname.setText(str_lastname)
                    p_profession.setText(str_profession)
                    p_address.setText(str_address)
                    p_age.setText(str_age)
                    p_telephon.setText(str_telephon)
                    Glide.with(applicationContext).load(usuario.getImagen()).placeholder(R.drawable.ic_item_user).into(p_image)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun actualizarInfo() {

        val str_name = p_name.text.toString()
        val str_lastname = p_lastname.text.toString()
        val str_profession = p_profession.text.toString()
        val str_address = p_address.text.toString()
        val str_age = p_age.text.toString()
        val str_telephon = p_telephon.text.toString()

        val hasmap = HashMap<String, Any>()

        hasmap["nombres"] = str_name
        hasmap["apellidos"] = str_lastname
        hasmap["profesion"] = str_profession
        hasmap["domicilio"] = str_address
        hasmap["edad"] = str_age
        hasmap["telefono"] = str_telephon

        referece!!.updateChildren(hasmap).addOnCompleteListener { task->
            if(task.isSuccessful){

                Toast.makeText(applicationContext, "Se han actualizado los datos", Toast.LENGTH_SHORT).show()

            } else {

                Toast.makeText(applicationContext, "ERROR: No sé han actualizado los datos", Toast.LENGTH_SHORT).show()

            }
        }.addOnFailureListener {e->

            Toast.makeText(applicationContext, "Ha ocurrido un error ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }

    private fun CuentaVerificada(){

        val btnEntendidoVerificado : MaterialButton
        val dialog = Dialog(this@PerfilActivity)

        dialog.setContentView(R.layout.cuadro_d_cuenta_verificada)

        btnEntendidoVerificado = dialog.findViewById(R.id.btnEntendidoVerificado)
        btnEntendidoVerificado.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)

    }


    private fun UpdateState(estado : String){
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios")
            .child(user!!.uid)

        val hasMap = HashMap<String, Any>()
        hasMap["estado"] = estado
        reference!!.updateChildren(hasMap)
    }

    override fun onResume() {
        super.onResume()

        UpdateState("onLine")
    }

    override fun onPause() {
        super.onPause()

        UpdateState("offLine")
    }

}