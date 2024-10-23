package com.pixl.crm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.telecalling.crm.R
import com.telecalling.crm.response.Instrested_calls_list_Response
import com.telecalling.crm.response.PhoneNumberX
import retrofit2.Callback

class LeadsAdapter()
//    private val context: List<PhoneNumberX>,
//
//    private val dataList: List<PhoneNumberX>,
//    private val leadcallClickListener: Callback<Instrested_calls_list_Response>
//) : RecyclerView.Adapter<LeadsAdapter.MyViewHolder>() {
//
//    interface leadCallClickListener {
//        fun leadonCallButtonClick(number: String)
//        fun leadonWhatsAppButtonClick(number: String)
//        fun leadonInfoClick(number: String, name: String, remarks: String, follow_up_date: String, lead_stage_status: String)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.intrestedcall_item, parent, false)
//        return MyViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val data = dataList[position]
//        holder.textViewNumber.text = data.number
//
//        holder.textViewUser.text = " Name  : ${data.name}"
//        holder.textViewUser.visibility = if (data.name.isNullOrEmpty() || data.name == "Default Name") View.GONE else View.VISIBLE
//
//        holder.followupdatetxt.text = " Follow up Date  : ${data.follow_up_date}"
//        holder.followupdatetxt.visibility = if (data.follow_up_date.isNullOrEmpty() || data.follow_up_date == "1970-01-01") View.GONE else View.VISIBLE
//
//        holder.textViewNumber.visibility = View.VISIBLE
//
//        holder.itemView.setOnClickListener {
//            holder.itemView.post {
//                notifyItemChanged(position)
//            }
//        }
//
//        holder.followupcall.setOnClickListener {
//
////            leadcallClickListener.leadonCallButtonClick(data.number)
//        }
//
//        holder.whatsappButton.setOnClickListener {
////            leadcallClickListener.leadonWhatsAppButtonClick(data.number)
//        }
//
//        holder.viewinfo.setOnClickListener {
////            leadcallClickListener.leadonInfoClick(data.number, data.name, data.remarks, data.follow_up_date, data.called_status)
//        }
//
//        // Set image based on lead status
//        when (data.called_status) {
//            "unknown" -> holder.statusleadimg?.setBackgroundResource(R.drawable.coldimg)
//            else -> holder.statusleadimg?.setBackgroundResource(R.drawable.hotstatus)
//        }
//    }
//
//    override fun getItemCount(): Int = dataList.size
//
//    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val textViewNumber: TextView = itemView.findViewById(R.id.leadstextViewNumber2)
//        val textViewUser: TextView = itemView.findViewById(R.id.leadstextViewUser2)
//        val followupdatetxt: TextView = itemView.findViewById(R.id.leadsfollowupdatetxt2)
//        val followupcall: ImageButton = itemView.findViewById(R.id.leadscallButton)
//        val whatsappButton: ImageButton = itemView.findViewById(R.id.leadswhatsappButton)
//        val viewinfo: TextView = itemView.findViewById(R.id.leadfollowupview_info)
//        val statusleadimg: View? = itemView.findViewById(R.id.leadsstatusleadimg)
//    }
//}