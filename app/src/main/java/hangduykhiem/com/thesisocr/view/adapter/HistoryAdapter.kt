package hangduykhiem.com.thesisocr.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.wolt.tacotaco.components.Command
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.domain.interactor.ResultArgs
import hangduykhiem.com.thesisocr.domain.model.OcrResultDomainModel
import hangduykhiem.com.thesisocr.view.controller.HistoryController

class HistoryAdapter(private val commandListener: (Command) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var items: List<OcrResultDomainModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val viewholder = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem_history, parent, false)
        return HistoryViewHolder(viewholder, commandListener)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    fun setItems(list: List<OcrResultDomainModel>) {
        this.items = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(historyViewHolder: HistoryViewHolder, position: Int) {
        historyViewHolder.bind(items[position])
    }

    class HistoryViewHolder(
        val holder: View, private val commandListener: (Command) -> Unit
    ) : RecyclerView.ViewHolder(holder) {
        private lateinit var item: OcrResultDomainModel

        val ivResult: ImageView = holder.findViewById(R.id.ivResult)
        val tvResultTime: TextView = holder.findViewById(R.id.tvResultTime)
        val tvResult: TextView = holder.findViewById(R.id.tvResultTime)

        init {
            holder.setOnClickListener {
                commandListener(HistoryController.GoToResultCommand(ResultArgs(ocrResultDomainModel = item)))
            }
        }


        fun bind(item: OcrResultDomainModel) {
            this.item = item
            Glide.with(holder).load(item.uri).into(ivResult)
            tvResultTime.setText(item.timestamp.toString())
            tvResult.setText(item.result)
        }
    }
}