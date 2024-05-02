package com.uintecs.klt.chatapp_kotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class Inicio : AppCompatActivity() {

//    private lateinit var btn_ir_registros : Button
    private lateinit var btn_ir_logeo : MaterialButton
    private lateinit var btn_login_google : MaterialButton

    var firebaseUser : FirebaseUser?=null

    private lateinit var auth : FirebaseAuth

    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInClient : GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        //supportActionBar!!.title = "Inicio"

//        btn_ir_registros = findViewById(R.id.btn_ir_registros)
        btn_ir_logeo = findViewById(R.id.btn_ir_logeo)
        btn_login_google = findViewById(R.id.btn_login_google)
        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        /*btn_ir_registros.setOnClickListener {

            val intent = Intent(this@Inicio, RegistroActivity::class.java)
            Toast.makeText(applicationContext, "Registros", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }*/

        btn_ir_logeo.setOnClickListener {

            val intent = Intent(this@Inicio, LoginActivity::class.java)
            //Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        btn_login_google.setOnClickListener {
            //Toast.makeText(applicationContext, "Login con Google", Toast.LENGTH_SHORT).show()
            empezarInicioSesionGoogle()
        }

    }

    private fun empezarInicioSesionGoogle() {
        //Si el usuario a seleccionado una cuenta de google
        val googleSignIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {resultado->
        if (resultado.resultCode == RESULT_OK) {

            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)
                AuntenticarGoogleFirebase(account.idToken)

            } catch (e: Exception) {

                Toast.makeText(applicationContext, "Ha ocurrido una excepción debido a ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("ERROR: ", "${e.message}")
            }

        } else {
            Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun AuntenticarGoogleFirebase(idToken: String?) {

        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult->
                /*Si el usuario es nuevo*/
                if (authResult.additionalUserInfo!!.isNewUser){
                    GuardarInfoBD()
                }
                /*Si el usuario ya se registró previamente*/
                else{
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }

            }.addOnFailureListener { e->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun GuardarInfoBD() {
        progressDialog.setMessage("Se está registrando su información...")
        progressDialog.show()

        /*Obtener información de una cuenta de Google*/
        val uidGoogle = auth.uid
        val correoGoogle = auth.currentUser?.email
        val n_Google = auth.currentUser?.displayName
        val nombre_usuario_G : String = n_Google.toString()

        val hashmap = HashMap<String, Any?>()
        hashmap["uid"] = uidGoogle
        hashmap["n_usuario"] = nombre_usuario_G
        hashmap["email"] = correoGoogle
        hashmap["imagen"] = ""
        hashmap["buscar"] = nombre_usuario_G.lowercase()

        /*Nuevos datos de usuario*/
        hashmap["nombres"] = ""
        hashmap["apellidos"] =""
        hashmap["edad"] = ""
        hashmap["profesion"] = ""
        hashmap["domicilio"] = ""
        hashmap["telefono"] = ""
        hashmap["estado"] = "offline"
        hashmap["proveedor"] = "Google"

        /*Referencia a la base de datos*/
        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(hashmap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                Toast.makeText(applicationContext, "Se ha registrado exitosamente", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun comprobarSesion() {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null){
            val intent = Intent(this@Inicio, MainActivity::class.java)
            Toast.makeText(applicationContext, "La sesión está activa", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        comprobarSesion()
        super.onStart()
    }
}