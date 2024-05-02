package com.uintecs.klt.chatapp_kotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var L_et_email : EditText
    private lateinit var L_et_password : EditText
    private lateinit var btn_login : Button
    private lateinit var auth : FirebaseAuth

    private lateinit var txt_ir_registro : TextView

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //supportActionBar!!.title = "Login"

        inicializarVariables()
        
        btn_login.setOnClickListener { 
            validarDatos()
        }

        txt_ir_registro.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistroActivity::class.java)
            startActivity(intent)
        }
        
    }

    private fun inicializarVariables(){

        L_et_email = findViewById(R.id.L_et_email)
        L_et_password = findViewById(R.id.L_et_password)
        btn_login = findViewById(R.id.btn_login)
        auth = FirebaseAuth.getInstance()

        txt_ir_registro = findViewById(R.id.txt_ir_registro)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Iniciando sesión")
        progressDialog.setCanceledOnTouchOutside(false)

    }

    private fun validarDatos() {
        
        val email : String = L_et_email.text.toString()
        val password : String = L_et_password.text.toString()
        
        if (email.isEmpty()){

            Toast.makeText(applicationContext, "Ingrese correo electrónico", Toast.LENGTH_SHORT).show()
        }
        
        if (password.isEmpty()){

            Toast.makeText(applicationContext, "Ingrese contraseña", Toast.LENGTH_SHORT).show()

        } else {
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {

        progressDialog.setMessage("Espere por favor")
        progressDialog.show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->

                if(task.isSuccessful){

                    progressDialog.dismiss()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    Toast.makeText(applicationContext, "Sesión Iniciada", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()

                } else{
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}