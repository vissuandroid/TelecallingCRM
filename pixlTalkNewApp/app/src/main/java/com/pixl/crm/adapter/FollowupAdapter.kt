import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pixl.crm.response.FollowupnumberResponse
import com.telecalling.crm.R
import android.content.Context
import android.media.Image
class FollowupAdapter(
    private val context: Context,
    private val dataList: List<FollowupnumberResponse>,
    private val callClickListener: CallClickListener
) : RecyclerView.Adapter<FollowupAdapter.MyViewHolder>() {

    interface CallClickListener {
        fun onCallButtonClick(number: String)
        fun onWhatsAppButtonClick(number: String)
        fun onInfoClick(number: String, name: String, remarks: String, follow_up_date: String, lead_stage_status: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_followup, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        holder.textViewNumber.text = data.number

        holder.company.text = data.person_name
        holder.company.visibility = if (data.person_name.isNullOrEmpty() || data.person_name == "Default Name") View.GONE else View.VISIBLE

        holder.remarksinfo.text = data.remarks
        holder.remarksinfo.visibility = if (data.remarks.isNullOrEmpty() || data.remarks == "Default Name") View.GONE else View.VISIBLE

//        holder.company.text = data.staff_name
//        holder.company.visibility = if (data.staff_name.isNullOrEmpty() || data.staff_name == "Default Name") View.GONE else View.VISIBLE

        holder.followupdatetxt.text = "Follow up : ${data.follow_up_date}"
        holder.followupdatetxt.visibility = if (data.follow_up_date.isNullOrEmpty() || data.follow_up_date == "1970-01-01") View.GONE else View.VISIBLE

        holder.itemView.setOnClickListener {
            holder.itemView.post {
                notifyItemChanged(position)
            }
        }

        holder.followupcall.setOnClickListener {
            callClickListener.onCallButtonClick(data.number)
        }

        holder.whatsappButton.setOnClickListener {
            callClickListener.onWhatsAppButtonClick(data.number)
        }

        holder.viewinfo.setOnClickListener {
            callClickListener.onInfoClick(data.number, data.person_name, data.remarks, data.follow_up_date, data.lead_stage_status)
        }

        // Set image based on lead status
        when (data.lead_stage_status) {
            "unknown" -> holder.statusleadimg.setBackgroundResource(R.drawable.coldimg)
            else -> holder.statusleadimg.setBackgroundResource(R.drawable.hotstatus)
        }
    }

    override fun getItemCount(): Int = dataList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNumber: TextView = itemView.findViewById(R.id.textViewMobileLabel)
        val company: TextView = itemView.findViewById(R.id.companyname)
        val followupdatetxt: TextView = itemView.findViewById(R.id.textViewUpdateLabel)
        val followupcall: ImageButton = itemView.findViewById(R.id.buttonCall)
        val whatsappButton: ImageButton = itemView.findViewById(R.id.buttonWhatsApp)
        val viewinfo: TextView = itemView.findViewById(R.id.textViewInfoLabel)
        val statusleadimg: View = itemView.findViewById(R.id.imageViewStatus)
        val remarksinfo: TextView = itemView.findViewById(R.id.remarkstxt)
    }
}
