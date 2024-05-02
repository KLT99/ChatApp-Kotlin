package com.uintecs.klt.chatapp_kotlin.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uintecs.klt.chatapp_kotlin.Adapters.AdapterUser
import com.uintecs.klt.chatapp_kotlin.Model.Usuario
import com.uintecs.klt.chatapp_kotlin.R


class FragmentUsers : Fragment() {

    private var userAdapter : AdapterUser?=null
    private var userList : List<Usuario>?=null
    private var rvUsers : RecyclerView?=null

    private lateinit var edt_search_user : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view : View = inflater.inflate(R.layout.fragment_users, container, false)

        rvUsers = view.findViewById(R.id.rv_users)
        rvUsers!!.setHasFixedSize(true)
        rvUsers!!.layoutManager = LinearLayoutManager(context)

        edt_search_user = view.findViewById(R.id.search_user)

        userList = ArrayList()
        getUsersDB()

        edt_search_user.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(s_user: CharSequence?, p1: Int, p2: Int, p3: Int) {

                searchUser(s_user.toString().lowercase())
            }

            override fun afterTextChanged(p0: Editable?) {
                //TODO("Not yet implemented")
            }

        })

        return view
    }

    private fun getUsersDB() {

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios").orderByChild("n_usuario")
        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                (userList as ArrayList<Usuario>).clear()

                if(edt_search_user.text.toString().isEmpty()){

                    for (sh in snapshot.children){

                        val usuario : Usuario?= sh.getValue(Usuario::class.java)

                        if(!(usuario!!.getUid()).equals(firebaseUser)){

                            (userList as ArrayList<Usuario>).add(usuario)
                        }
                    }

                    userAdapter = AdapterUser(context!!, userList!!, false)
                    rvUsers!!.adapter = userAdapter
                }

            }

            override fun onCancelled(error: DatabaseError) {


            }
        })
    }

    private fun searchUser(SearchUser : String){

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val search = FirebaseDatabase.getInstance().reference.child("Usuarios").orderByChild("buscar")
            .startAt(SearchUser).endAt(SearchUser + "\uf8ff")
        search.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                (userList as ArrayList<Usuario>).clear()

                for (sh in snapshot.children){

                    val usuario : Usuario?= sh.getValue(Usuario::class.java)

                    if(!(usuario!!.getUid()).equals(firebaseUser)){

                        (userList as ArrayList<Usuario>).add(usuario)
                    }
                }

                userAdapter = AdapterUser(context!!, userList!!, false)
                rvUsers!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

        })

    }
}