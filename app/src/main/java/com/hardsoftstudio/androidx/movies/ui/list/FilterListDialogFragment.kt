package com.hardsoftstudio.androidx.movies.ui.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hardsoftstudio.androidx.movies.R
import com.hardsoftstudio.androidx.movies.domain.MoviesRepository
import kotlinx.android.synthetic.main.fragment_filter_list_dialog.*
import kotlinx.android.synthetic.main.fragment_filter_list_dialog_item.view.*

const val ARG_FILTERS = "filters"

class FilterListDialogFragment : BottomSheetDialogFragment() {

    companion object {

        fun newInstance(filters: List<MoviesRepository.Filter>): FilterListDialogFragment {
            return FilterListDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_FILTERS, ArrayList(filters))
                }
            }
        }
    }

    private var selectionListener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val filters = arguments?.getParcelableArrayList<MoviesRepository.Filter>(ARG_FILTERS).orEmpty()
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = FilterAdapter(filters)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        selectionListener = if (parent != null) {
            parent as Listener
        } else {
            context as Listener
        }
    }

    override fun onDetach() {
        selectionListener = null
        super.onDetach()
    }

    interface Listener {
        fun onFilterSelected(filter: MoviesRepository.Filter)
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_filter_list_dialog_item, parent, false)) {

        fun bind(filter: MoviesRepository.Filter) {
            itemView.filterView.apply {
                text = when (filter) {
                    is MoviesRepository.Filter.Upcoming -> getString(R.string.filter_upcoming)
                    is MoviesRepository.Filter.Year -> getString(R.string.filter_year, filter.year)
                    is MoviesRepository.Filter.Date -> getString(R.string.filter_oldies)
                }

                setOnClickListener {
                    selectionListener?.onFilterSelected(filter)
                }
            }
        }
    }

    private inner class FilterAdapter(private val filters: List<MoviesRepository.Filter>)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(filters[position])
        }

        override fun getItemCount(): Int {
            return filters.size
        }
    }
}
