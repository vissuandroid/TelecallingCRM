package com.telecalling.crm.adapter

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.pixl.crm.response.FollowupnumberResponse
import com.pixl.pixltalknewapp.R
import com.telecalling.crm.R
import com.telecalling.crm.response.PhoneNumberX
import java.util.Calendar
class IntrestescallAdapter(
    private val context: Context,
    private val callClickListener: IntrestedCallClickListener,
    private val dataList: List<PhoneNumberX>
) : RecyclerView.Adapter<IntrestescallAdapter.ViewHolder>() {

    interface IntrestedCallClickListener {
        fun onCallButtonClick(number: String)
        fun onWhatsAppButtonClick(number: String)
        fun onInfoClick( number: String,
                         name: String,
                         remarks: String,
                         followUpDate: String,
                         calledStatus: String,
                         callStatus: String,
                         dateAdded: String,
                         dealStatus: String,
                         dealAmount: Double,
                         totalCalls: Int,
                         leadStageId: Int,
                         leadStageStatus: String, id: Int,)

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val infoTextViewName: TextView = itemView.findViewById(R.id.intrestedtextViewUser2)
        val infoTextViewNumber: TextView = itemView.findViewById(R.id.intrestedtextViewNumber2)
        val infoStatusIcon: ImageView = itemView.findViewById(R.id.intrestedstatusleadimg)
        val infoTextViewCreatedOn: TextView = itemView.findViewById(R.id.intrestedfollowupdatetxt2)
        val intrestwhatsappmsg: ImageButton = itemView.findViewById(R.id.intrestedfollowupwhatsappButton)
        val intrestcall: ImageButton = itemView.findViewById(R.id.intrestedfollowcallbutton)
        val viewinfointrested: TextView = itemView.findViewById(R.id.interstedfollowupview_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.intrestedcall_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]

        // Safely set text with default values if null
        holder.infoTextViewName.text = currentItem.name?.takeIf { it.isNotBlank() } ?: "Unknown"
        holder.infoTextViewNumber.text = currentItem.number ?: "No Number"

        // Display "Not Specified" if followUpDate is null or empty
        holder.infoTextViewCreatedOn.text = "Followup: ${currentItem.follow_up_date ?: "Not specified"}"

        // Update the icon based on the lead_stage_status
        when (currentItem.lead_stage_status?.trim()?.lowercase()) {
            "cold" -> holder.infoStatusIcon.setImageResource(R.drawable.coldimg)
            "warm" -> holder.infoStatusIcon.setImageResource(R.drawable.warm)
            "hot" -> holder.infoStatusIcon.setImageResource(R.drawable.hotstatus)
            else -> holder.infoStatusIcon.setImageResource(R.drawable.hotstatus) // Default image
        }
        holder.infoTextViewCreatedOn.text = "Followup: ${currentItem.follow_up_date ?: "Not specified"}"

        // Visibility checks
//        holder.infoTextViewName.visibility = if (currentItem.name.isNullOrEmpty() || currentItem.name == "Default Name") View.GONE else View.VISIBLE
//        holder.infoTextViewCreatedOn.visibility = if (currentItem.followUpDate.isNullOrEmpty() || currentItem.followUpDate == "1970-01-01") View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener {
            holder.itemView.post {
                notifyItemChanged(position)
            }
        }

        holder.intrestcall.setOnClickListener {
            currentItem.number?.let { number ->
                callClickListener.onCallButtonClick(number)
            }
        }

        holder.intrestwhatsappmsg.setOnClickListener {
            currentItem.number?.let { number ->
                callClickListener.onWhatsAppButtonClick(number)
            }
        }

        holder.viewinfointrested.setOnClickListener {
            Log.d("IntrestescallAdapter", "View Info clicked for ${currentItem.number}")
            callClickListener.onInfoClick(
                number = currentItem.number ?: "",
                name = currentItem.name ?: "",
                remarks = currentItem.remarks ?: "",
                followUpDate = currentItem.follow_up_date ?: "Not specified",
                calledStatus = currentItem.calledStatus ?: "Unknown",
                callStatus = currentItem.callStatus ?: "Unknown",
                dateAdded = currentItem.date_added ?: "Unknown",
                dealStatus = currentItem.dealStatus ?: "Unknown",
                dealAmount = currentItem.dealAmount ?: 0.0,
                totalCalls = currentItem.totalCalls,
                leadStageId = currentItem.leadStageId ?: 0,
                leadStageStatus = currentItem.lead_stage_status ?: "Unknown",
                id = currentItem.id
            )
        }
    }

    override fun getItemCount() = dataList.size
}
