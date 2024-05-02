package com.uintecs.klt.chatapp_kotlin.Fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.uintecs.klt.chatapp_kotlin.Adapters.AdapterUser
import com.uintecs.klt.chatapp_kotlin.Model.ListaChats
import com.uintecs.klt.chatapp_kotlin.Model.Usuario
import com.uintecs.klt.chatapp_kotlin.Notificaciones.Token
import com.uintecs.klt.chatapp_kotlin.R


class FragmentChats : Fragment() {

    private var usuarioAdapter : AdapterUser ?= null
    private var usuarioList : List<Usuario> ?= null
    private var usuarioListaChats : List<ListaChats> ?= null

    lateinit var rv_listaChats : RecyclerView
    private var firebaseUser : FirebaseUser ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_chats, container, false)

        rv_listaChats = view.findViewById(R.id.rv_listaChats)
        rv_listaChats.setHasFixedSize(true)
        rv_listaChats.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usuarioListaChats = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ListaMensajes").child(firebaseUser!!.uid)

        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usuarioListaChats as ArrayList).clear()

                for(dataSnapshot in snapshot.children){

                    val chatList = dataSnapshot.getValue(ListaChats::class.java)
                    (usuarioListaChats as ArrayList).add(chatList!!)
                }
                recuperarListaChats()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener {tarea->

                if (tarea.isSuccessful){
                    if (tarea.result != null && !TextUtils.isEmpty(tarea.result)){

                        val token : String = tarea.result!!
                        
                        actualizarToken(token)

                    }
                }

            }

        return view
    }

    private fun actualizarToken(token: String) {

        val reference = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)

        reference.child(firebaseUser!!.uid).setValue(token1)

    }

    private fun recuperarListaChats(){

        usuarioList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios")
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usuarioList as ArrayList).clear()
                for (dataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(Usuario::class.java)
                    for (cadaLista in usuarioListaChats!!){
                        if(user!!.getUid().equals(cadaLista.getUid())){
                            (usuarioList as ArrayList).add(user!!)
                        }
                    }
                    usuarioAdapter = AdapterUser(context!!, (usuarioList as ArrayList<Usuario>), true)
                    rv_listaChats.adapter = usuarioAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}