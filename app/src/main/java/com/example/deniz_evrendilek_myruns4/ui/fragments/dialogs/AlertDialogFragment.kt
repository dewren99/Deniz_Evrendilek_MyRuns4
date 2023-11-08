package com.example.deniz_evrendilek_myruns4.ui.fragments.dialogs

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment

interface AlertDialogOnClickListener {
    fun onListItemClicked(position: Int)
}

class AlertDialogFragment(
    private val title: String,
    private val inputType: Int?,
    private val placeHolder: String?,
    private val positiveButtonText: String?,
    private val negativeButtonText: String?,
    private val positiveButtonCallback: ((DialogInterface, Int, String) -> Unit)?,
    private val negativeButtonCallback: ((DialogInterface, Int) -> Unit)?,
    private val listItems: Array<String>?,
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createAlertDialog()
    }

    private fun createAlertDialog(): Dialog {
        val builder = Builder(requireActivity())
        val input = EditText(requireActivity())

        builder.setTitle(title)
        setBody(builder, input)

        return builder.create()
    }

    private fun setBody(builder: Builder, input: EditText) {
        if (listItems != null) {
            setList(builder)
            return
        }

        setInputType(input)
        builder.setView(input)
        setButtons(builder, input)
    }

    private fun setInputType(input: EditText) {
        if (inputType == null) {
            return
        }
        input.inputType = inputType
        if (inputType == InputType.TYPE_TEXT_FLAG_MULTI_LINE) {
            input.isSingleLine = false
            input.minLines = 3
        }
        if (placeHolder != null) {
            input.hint = placeHolder
        }
    }

    private fun setButtons(builder: Builder, input: EditText) {
        if (positiveButtonCallback != null && positiveButtonText != null) {
            builder.setPositiveButton(positiveButtonText) { dialog, i ->
                positiveButtonCallback.invoke(dialog, i, input.text.toString())
            }
        }

        if (negativeButtonCallback != null && negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText) { dialog, i ->
                negativeButtonCallback.invoke(dialog, i)
            }
        }
    }

    private fun setList(builder: Builder) {
        if (listItems == null) {
            return
        }
        builder.setItems(listItems) { _, which -> onListItemClicked(which) }
    }

    private fun onListItemClicked(position: Int) {
        (parentFragment as? AlertDialogOnClickListener)?.onListItemClicked(position)
    }
}