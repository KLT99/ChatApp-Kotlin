package com.uintecs.klt.chatapp_kotlin

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uintecs.klt.chatapp_kotlin.Fragments.FragmentChats
import com.uintecs.klt.chatapp_kotlin.Fragments.FragmentUsers
import com.uintecs.klt.chatapp_kotlin.Model.Chat
import com.uintecs.klt.chatapp_kotlin.Model.Usuario
import com.uintecs.klt.chatapp_kotlin.Perfil.PerfilActivity

class MainActivity : AppCompatActivity() {

    var reference : DatabaseReference?=null
    var firebaseUser : FirebaseUser?=null
    private lateinit var user_name : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarComponentes()
        obtenerDato()
    }

    private fun inicializarComponentes(){

        val toolbar : Toolbar = findViewById(R.id.toolBarMain)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseUser!!.uid)
        user_name = findViewById(R.id.user_name)

        val tabLayout : TabLayout = findViewById(R.id.tabLayoutMain)
        val viewPager : ViewPager = findViewById(R.id.viewPagerMain)

        /*val viewPagerAdapter = viewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addItem(FragmentUsers(), "Usuarios")
        viewPagerAdapter.addItem(FragmentChats(), "Chats")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)*/

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val viewPagerAdapter = viewPagerAdapter(supportFragmentManager)
                var contMensajesNoLeidos = 0

                for (dataSnapshot in snapshot.children){

                    val chat = dataSnapshot.getValue(Chat::class.java)

                    if(chat!!.getReceptor().equals(firebaseUser!!.uid) && !chat.isVisto()){

                        contMensajesNoLeidos += 1

                    }
                }

                if (contMensajesNoLeidos == 0){
                    viewPagerAdapter.addItem(FragmentChats(), "Chats")
                } else {
                    viewPagerAdapter.addItem(FragmentChats(), "[$contMensajesNoLeidos] Chats")
                }
                viewPagerAdapter.addItem(FragmentUsers(), "Usuarios")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun obtenerDato(){

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                    user_name.text = usuario!!.getN_Usuario()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    class viewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

        private val listaFragmentos : MutableList<Fragment> = ArrayList()
        private val listaTitulos : MutableList<String> = ArrayList()

        override fun getCount(): Int {

            return listaFragmentos.size
        }

        override fun getItem(position: Int): Fragment {

            return listaFragmentos[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return listaTitulos[position]
        }

        fun addItem(fragment: Fragment, titulo:String){

            listaFragmentos.add(fragment)
            listaTitulos.add(titulo)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){

            R.id.menu_perfil->{

                val intent = Intent(applicationContext, PerfilActivity::class.java)
                startActivity(intent)

                return true
            }

            R.id.menu_acerca_de->{

                InfoApp()
                return true
            }

            R.id.menu_salir->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, Inicio::class.java)
                Toast.makeText(applicationContext, "Has cerrado sesión", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                //finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun InfoApp(){

        val EntendidoInfo : Button
        val ir_FB : ImageView
        val ir_instagram : ImageView
        val ir_youtube : ImageView
        val ir_twitter : ImageView

        val dialog = Dialog(this@MainActivity)

        dialog.setContentView(R.layout.cuadro_info_app)
        EntendidoInfo = dialog.findViewById(R.id.entendidoInfo)
        ir_FB = dialog.findViewById(R.id.Ir_FB)
        ir_instagram = dialog.findViewById(R.id.Ir_Instagram)
        ir_youtube = dialog.findViewById(R.id.Ir_youtube)
        ir_twitter = dialog.findViewById(R.id.Ir_twitter)

        ir_FB.setOnClickListener {
            redSocial("https://www.facebook.com")
        }

        ir_instagram.setOnClickListener {
            redSocial("https://www.instagram.com")
        }

        ir_youtube.setOnClickListener {
            redSocial("https://www.youtube.com")
        }

        ir_twitter.setOnClickListener {
            redSocial("https://www.twitter.com")
        }

        EntendidoInfo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)

    }

    private fun redSocial(redSocial : String){
        val RS = Intent(Intent.ACTION_VIEW, Uri.parse(redSocial))
        startActivity(RS)
    }

    private fun UpdateState(estado : String){
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios")
            .child(firebaseUser!!.uid)

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