package com.uintecs.klt.chatapp_kotlin.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.uintecs.klt.chatapp_kotlin.Model.Chat
import com.uintecs.klt.chatapp_kotlin.R

class AdapterChat (context: Context, chatList: List<Chat>, imgUrl: String)
    : RecyclerView.Adapter<AdapterChat.ViewHolder?>() {

    private val context : Context
    private val chatList : List<Chat>
    private val imgUrl : String
    var firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    init {
        this.context = context
        this.chatList = chatList
        this.imgUrl = imgUrl
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        /*VISTA MENSAJE IZQUIERDO*/
        var img_perfil_msg : ImageView?= null
        var txt_view_msg : TextView?= null
        var img_send_left : ImageView?= null
        var txt_msg_visto : TextView?= null

        /*VISTA MENSAJE DERECHO*/
        var img_send_right : ImageView?=null

        init {
            img_perfil_msg = itemView.findViewById(R.id.img_perfil_msg)
            txt_view_msg = itemView.findViewById(R.id.txt_view_msg)
            img_send_left = itemView.findViewById(R.id.img_send_left)
            txt_msg_visto = itemView.findViewById(R.id.txt_msg_visto)

            img_send_right = itemView.findViewById(R.id.img_send_right)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {

        return if(position == 1){

            val view : View = LayoutInflater.from(context).inflate(com.uintecs.klt.chatapp_kotlin.R.layout.item_msg_right, parent, false)
            ViewHolder(view)

        } else {
            val view : View = LayoutInflater.from(context).inflate(com.uintecs.klt.chatapp_kotlin.R.layout.item_msg_left, parent, false)
            ViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat : Chat = chatList[position]

        Glide.with(context).load(imgUrl).placeholder(R.drawable.ic_image_chat).into(holder.img_perfil_msg!!)

        //obtenedremos el mensaje y si el mensaje es igual a "se ha enviado mensaje" y la URL no esta vacio que enviamos una imagen
        if(chat.getMensaje().equals("Se ha enviado la imagen") && !chat.getUrl().equals("")){

            /*Condición que indica que nosotros estamos enviando el mensaje*/
            if(chat.getEmisor().equals(firebaseUser!!.uid)){

                holder.txt_view_msg!!.visibility = View.GONE
                holder.img_send_right!!.visibility = View.VISIBLE

                Glide.with(context).load(chat.getUrl()).placeholder(R.drawable.ic_image_send).into(holder.img_send_right!!)

            } //usuario el cual nos envia una imagen como mensaje
            else if (!chat.getEmisor().equals(firebaseUser!!.uid)){

                holder.txt_view_msg!!.visibility = View.GONE
                holder.img_send_left!!.visibility = View.VISIBLE

                Glide.with(context).load(chat.getUrl()).placeholder(R.drawable.ic_image_send).into(holder.img_send_left!!)

            }

            // si el mensaje contiene sólo texto
        } else {

            holder.txt_view_msg!!.text = chat.getMensaje()
            //Toast.makeText(context, "mensajes: "+chat.getMensaje(), Toast.LENGTH_SHORT).show()
        }

        //Mensaje enviado y visto
        if(position == chatList.size-1){ //comprueba si el mensaje fue visto

            if(chat.isVisto()){

                holder.txt_msg_visto!!.text = "Visto"

                if(chat.getMensaje().equals("Se ha enviado la imagen") && !chat.getUrl().equals("")){

                    val lp : RelativeLayout.LayoutParams = holder.txt_msg_visto!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0,245,10,0)
                    holder.txt_msg_visto!!.layoutParams = lp
                }
            } else {
                holder.txt_msg_visto!!.text = "Enviado"

                if(chat.getMensaje().equals("Se ha enviado la imagen") && !chat.getUrl().equals("")){

                    val lp : RelativeLayout.LayoutParams = holder.txt_msg_visto!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0,245,10,0)
                    holder.txt_msg_visto!!.layoutParams = lp

                }
            }

        } else {
            holder.txt_msg_visto!!.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {

        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {

        return if (chatList[position].getEmisor().equals(firebaseUser!!.uid)){
            1
        } else {
            0
        }
    }

}