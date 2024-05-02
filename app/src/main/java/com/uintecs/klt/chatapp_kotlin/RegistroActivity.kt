package com.uintecs.klt.chatapp_kotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var r_et_nombre_user : EditText
    private lateinit var r_et_email :EditText
    private lateinit var r_et_password : EditText
    private lateinit var r_et_r_password : EditText
    private lateinit var btn_registrar : Button

    private lateinit var auth : FirebaseAuth
    private lateinit var reference : DatabaseReference

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        //supportActionBar!!.title = "Registros"
        inicializarVariables()

        btn_registrar.setOnClickListener {
            validarDatos()
        }
    }

    private fun inicializarVariables(){

        r_et_nombre_user = findViewById(R.id.r_et_nombre_usuario)
        r_et_email = findViewById(R.id.r_et_email)
        r_et_password = findViewById(R.id.r_et_password)
        r_et_r_password = findViewById(R.id.r_et_r_password)
        btn_registrar = findViewById(R.id.btn_registrar)

        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Registrando información")
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun validarDatos() {

        val nombre_usuario : String = r_et_nombre_user.text.toString()
        val email : String = r_et_email.text.toString()
        val password : String = r_et_password.text.toString()
        val r_password : String = r_et_r_password.text.toString()

        if (nombre_usuario.isEmpty()) {

            Toast.makeText(applicationContext, "Ingrese nombre de usuario", Toast.LENGTH_SHORT).show()

        } else if (email.isEmpty()) {

            Toast.makeText(applicationContext, "Ingrese su correo", Toast.LENGTH_SHORT).show()

        } else if (password.isEmpty()) {

            Toast.makeText(applicationContext, "Ingrese contraseña", Toast.LENGTH_SHORT).show()

        } else if (r_password.isEmpty()) {

            Toast.makeText(applicationContext, "Por favor repita contraseña", Toast.LENGTH_SHORT).show()

        } else if (!password.equals(r_password)) {

            Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()

        } else {

            registrarUsuario(email, password)

        }
    }

    private fun registrarUsuario(email: String, password: String) {

        progressDialog.setMessage("Espere por favor")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->

                if (task.isSuccessful) {

                    progressDialog.dismiss()

                    var uid : String = ""

                    uid = auth.currentUser!!.uid
                    //nombre de la base de datos
                    reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(uid)

                    val hashmap = HashMap<String, Any>()
                    val h_nombre_usuario : String = r_et_nombre_user.text.toString()
                    val h_email : String = r_et_email.text.toString()

                    hashmap["uid"] = uid
                    hashmap["n_usuario"] = h_nombre_usuario
                    hashmap["email"] = h_email
                    hashmap["imagen"] = ""
                    hashmap["buscar"] = h_nombre_usuario.lowercase()

                    // NUEVOS DATOS DE USUARIO
                    hashmap["nombres"] = ""
                    hashmap["apellidos"] = ""
                    hashmap["edad"] = ""
                    hashmap["profesion"] = ""
                    hashmap["domicilio"] = ""
                    hashmap["telefono"] = ""
                    hashmap["estado"] = "offLine"
                    hashmap["proveedor"] = "Email"


                    reference.updateChildren(hashmap).addOnCompleteListener {task2->
                        if (task2.isSuccessful) {

                            val intent = Intent(this@RegistroActivity, MainActivity::class.java)
                            Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            startActivity(intent)

                        }
                    }
                        .addOnFailureListener {e->

                        Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()

                    }

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                }

        }.addOnFailureListener {e2->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e2.message}", Toast.LENGTH_LONG).show()
        }

    }

}