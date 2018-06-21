package com.simplemobiletools.gallery.dialogs

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.View
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.setupDialogStuff
import com.simplemobiletools.gallery.R
import com.simplemobiletools.gallery.extensions.config
import com.simplemobiletools.gallery.helpers.*
import kotlinx.android.synthetic.main.dialog_change_grouping.view.*

class ChangeGroupingDialog(val activity: BaseSimpleActivity, val isShowingAll: Boolean, val path: String = "", val callback: () -> Unit) :
        DialogInterface.OnClickListener {
    private var currGrouping = 0
    private var config = activity.config
    private var view: View

    init {
        view = activity.layoutInflater.inflate(R.layout.dialog_change_grouping, null).apply {
            use_for_this_folder_divider.beVisibleIf(!isShowingAll)
            grouping_dialog_use_for_this_folder.beVisibleIf(!isShowingAll)
            grouping_dialog_use_for_this_folder.isChecked = config.hasCustomGrouping(path)
            grouping_dialog_radio_folder.beVisibleIf(isShowingAll)
        }

        AlertDialog.Builder(activity)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
                    activity.setupDialogStuff(view, this, R.string.group_by)
                }

        currGrouping = config.getFolderGrouping(path)
        setupGroupRadio()
        setupOrderRadio()
    }

    private fun setupGroupRadio() {
        val groupingRadio = view.grouping_dialog_radio_grouping

        val groupBtn = when {
            currGrouping and GROUP_BY_NONE != 0 -> groupingRadio.grouping_dialog_radio_none
            currGrouping and GROUP_BY_LAST_MODIFIED != 0 -> groupingRadio.grouping_dialog_radio_last_modified
            currGrouping and GROUP_BY_DATE_TAKEN != 0 -> groupingRadio.grouping_dialog_radio_date_taken
            currGrouping and GROUP_BY_FILE_TYPE != 0 -> groupingRadio.grouping_dialog_radio_file_type
            currGrouping and GROUP_BY_EXTENSION != 0 -> groupingRadio.grouping_dialog_radio_extension
            else -> groupingRadio.grouping_dialog_radio_folder
        }
        groupBtn.isChecked = true
    }

    private fun setupOrderRadio() {
        val orderRadio = view.grouping_dialog_radio_order
        var orderBtn = orderRadio.grouping_dialog_radio_ascending

        if (currGrouping and GROUP_DESCENDING != 0) {
            orderBtn = orderRadio.grouping_dialog_radio_descending
        }
        orderBtn.isChecked = true
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        val groupingRadio = view.grouping_dialog_radio_grouping
        var grouping = when (groupingRadio.checkedRadioButtonId) {
            R.id.grouping_dialog_radio_none -> GROUP_BY_NONE
            R.id.grouping_dialog_radio_last_modified -> GROUP_BY_LAST_MODIFIED
            R.id.grouping_dialog_radio_date_taken -> GROUP_BY_DATE_TAKEN
            R.id.grouping_dialog_radio_file_type -> GROUP_BY_FILE_TYPE
            R.id.grouping_dialog_radio_extension -> GROUP_BY_EXTENSION
            else -> GROUP_BY_FOLDER
        }

        if (view.grouping_dialog_radio_order.checkedRadioButtonId == R.id.grouping_dialog_radio_descending) {
            grouping = grouping or GROUP_DESCENDING
        }

        if (view.grouping_dialog_use_for_this_folder.isChecked) {
            config.saveFolderGrouping(path, grouping)
        } else {
            config.removeFolderGrouping(path)
            config.groupBy = grouping
        }
        callback()
    }
}