package com.ishihata_tech.hamiot_client.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.HistoryFragmentBinding
import com.ishihata_tech.hamiot_client.ui.common.ProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    companion object {
        private const val PROGRESS_DIALOG_TAG = "progressDialog"
    }

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var listViewAdapter: ListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listViewAdapter = ListViewAdapter(viewModel)

        // リスト更新の監視
        viewModel.list.observe(this) {
            listViewAdapter.notifyDataSetChanged()
        }

        // 読み込み中状態の監視
        viewModel.isLoading.observe(this) {
            when (it) {
                true -> {
                    ProgressDialogFragment().show(parentFragmentManager, PROGRESS_DIALOG_TAG)
                }
                false -> {
                    (parentFragmentManager
                        .findFragmentByTag(PROGRESS_DIALOG_TAG) as? ProgressDialogFragment)
                        ?.dismiss()
                }
            }
        }

        // エラーの監視
        lifecycleScope.launch {
            viewModel.errorMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<HistoryFragmentBinding>(inflater, R.layout.history_fragment, container, false).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        // ListViewのアダプタ
        binding.list.adapter = listViewAdapter

        // 戻るボタン
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private class ListViewAdapter(
        private val viewModel: HistoryViewModel,
    ) : BaseAdapter() {
        companion object {
            private const val VIEW_TYPE_TRANSACTION = 0
            private const val VIEW_TYPE_NEXT = 1
        }

        override fun getCount(): Int {
            return (viewModel.list.value?.size ?: 0) +
                    if (viewModel.nextPageHash.isNullOrEmpty()) 0 else 1
        }

        override fun getItem(position: Int): HistoryViewModel.Row {
            return viewModel.list.value!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getViewTypeCount(): Int {
            return 2
        }

        override fun getItemViewType(position: Int): Int {
            return if (position < viewModel.list.value!!.size)
                VIEW_TYPE_TRANSACTION else VIEW_TYPE_NEXT
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return when (getItemViewType(position)) {
                VIEW_TYPE_TRANSACTION -> {
                    val row = getItem(position)
                    convertView ?: LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_transaction, parent, false).apply {
                            findViewById<TextView>(R.id.text_time).text = row.time
                            findViewById<TextView>(R.id.text_direction).setText(
                                if (row.isReceived) R.string.received else R.string.sent)
                            findViewById<TextView>(R.id.text_opponent).text = row.opponent
                            findViewById<TextView>(R.id.text_amount).text = row.amount
                        }
                }
                else -> {
                    convertView ?: LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_item_next, parent, false).apply {
                            findViewById<Button>(R.id.button_next).setOnClickListener {
                                viewModel.loadPage()
                            }
                        }
                }
            }
        }
    }
}