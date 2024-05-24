package com.uintecs.klt.chatapp_kotlin.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uintecs.klt.chatapp_kotlin.Chat.MessageActivity
import com.uintecs.klt.chatapp_kotlin.Model.Usuario
import com.uintecs.klt.chatapp_kotlin.R

class AdapterUser (context : Context, listaUsuarios : List<Usuario>, chatLeido : Boolean) : RecyclerView.Adapter<AdapterUser.ViewHolder?>(){

    private val context : Context
    private val listaUsuarios : List<Usuario>
    private var chatLeido : Boolean

    init {
        this.context = context
        this.listaUsuarios = listaUsuarios
        this.chatLeido = chatLeido
    }

    class ViewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {

        var user_name : TextView
        //var user_email : TextView
        var user_image : ImageView
        var img_online : ImageView
        var img_offLine : ImageView

        init {
            user_name = itemView.findViewById(R.id.item_user_name)
            //user_email = itemView.findViewById(R.id.item_user_email)
            user_image = itemView.findViewById(R.id.item_image)
            img_online = itemView.findViewById(R.id.img_online)
            img_offLine = itemView.findViewById(R.id.img_offline)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view : View = LayoutInflater.from(context).inflate(R.layout.item_users, parent, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {

        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val usuario : Usuario = listaUsuarios[position]

        holder.user_name.text = usuario.getN_Usuario()
        //holder.user_email.text = usuario.getEmail()
        Glide.with(context).load(usuario.getImagen()).placeholder(R.drawable.ic_item_user).into(holder.user_image)

        holder.itemView.setOnClickListener{

            val intent = Intent(context, MessageActivity::class.java)

            intent.putExtra("uid_usuario", usuario.getUid())
            //Toast.makeText(context, "El usuario seleccionado es: "+usuario.getN_Usuario(), Toast.LENGTH_SHORT).show()
            context.startActivity(intent)
        }

        if (chatLeido){

            if (usuario.getEstado() == "onLine"){

                holder.img_online.visibility = View.VISIBLE
                holder.img_offLine.visibility = View.GONE

            } else {

                holder.img_online.visibility = View.GONE
                holder.img_offLine.visibility = View.VISIBLE

            }

        } else {

            holder.img_online.visibility = View.GONE
            holder.img_offLine.visibility = View.GONE

        }

    }

}