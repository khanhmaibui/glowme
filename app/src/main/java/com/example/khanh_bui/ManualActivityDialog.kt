package com.example.khanh_bui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class ManualActivityDialog : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        const val DIALOG_KEY = "dialog"
        const val DURATION_DIALOG = 1
        const val DISTANCE_DIALOG = 2
        const val CALORIES_DIALOG = 3
        const val HEART_RATE_DIALOG = 4
        const val COMMENT_DIALOG = 5
    }
    private lateinit var editText: EditText


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var returnDialog: Dialog
        val bundle = arguments
        val dialogId = bundle?.getInt(DIALOG_KEY)
        val alertDialogBuilder = AlertDialog.Builder(requireActivity())
        val view: View = requireActivity().layoutInflater.inflate(R.layout.fragment_dialog, null)
        editText = view.findViewById(R.id.fragment_dialog)
        alertDialogBuilder.setView(view)
        alertDialogBuilder.setPositiveButton("OK", this)
        alertDialogBuilder.setNegativeButton("CANCEL", this)
        if (dialogId == DURATION_DIALOG) {
            alertDialogBuilder.setTitle("Duration")
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        } else if (dialogId == DISTANCE_DIALOG) {
            alertDialogBuilder.setTitle("Distance")
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        } else if (dialogId == CALORIES_DIALOG) {
            alertDialogBuilder.setTitle("Calories")
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        } else if (dialogId == HEART_RATE_DIALOG) {
            alertDialogBuilder.setTitle("Heart Rate")
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        } else if (dialogId == COMMENT_DIALOG) {
            alertDialogBuilder.setTitle("Comment")
            editText.hint = "How did it go? Notes here."
        }

        returnDialog = alertDialogBuilder.create()
        return returnDialog
    }

    override fun onClick(dialog: DialogInterface?, item: Int) {

    }
}