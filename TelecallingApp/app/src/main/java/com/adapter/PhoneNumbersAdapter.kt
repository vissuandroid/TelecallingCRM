package com.telecalling.crm.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.telecalling.crm.R
import com.telecalling.crm.response.PhoneNumber
class PhoneNumbersAdapter(var phoneNumbers: MutableList<PhoneNumber>) :
    RecyclerView.Adapter<PhoneNumbersAdapter.PhoneNumberViewHolder>() {

    class PhoneNumberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val phoneNumberTextView: TextView? = view.findViewById(R.id.phoneNumberTextView11)
        val nameTextView: TextView? = view.findViewById(R.id.nameField)

        init {
            if (phoneNumberTextView == null || nameTextView == null) {
                android.util.Log.e("PhoneNumbersAdapter", "View IDs not found. Check XML layout.")
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneNumberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.phonenumbertextview, parent, false)
        return PhoneNumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneNumberViewHolder, position: Int) {
        val phoneNumber = phoneNumbers[position]
        holder.phoneNumberTextView?.text = phoneNumber.number

        holder.nameTextView?.setVisibility(View.VISIBLE)

        if (phoneNumber.name.isNullOrEmpty()) {
            // If name is null or empty, hide the name TextView
//            holder.nameTextView?.visibility = View.GONE
            holder.nameTextView?.text = "Unknown"
        } else {
            // If name is present, show the name and phone number
//            holder.nameTextView?.visibility = View.VISIBLE
            holder.nameTextView?.text = phoneNumber.name
        }
    }

    override fun getItemCount(): Int = phoneNumbers.size

    fun updatePhoneNumbers(newNumbers: List<PhoneNumber>) {
        phoneNumbers.clear()
        phoneNumbers.addAll(newNumbers)
        notifyDataSetChanged()
    }
}

