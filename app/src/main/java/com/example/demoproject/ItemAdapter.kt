import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.demoproject.R
import com.example.demoproject.model.PatientData

class ItemAdapter(private val context: Context, private val itemList: List<PatientData>) : BaseAdapter() {

    override fun getCount(): Int = itemList.size

    override fun getItem(position: Int): Any = itemList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val date: TextView = view.findViewById(R.id.date)
        val angle: TextView = view.findViewById(R.id.angle)
        val currentItem: PatientData = getItem(position) as PatientData
        date.text = currentItem.date
        angle.text = currentItem.angle

        return view
    }
}
