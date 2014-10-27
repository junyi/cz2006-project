package com.foodsurvey.foodsurvey.utility;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.foodsurvey.foodsurvey.R;

import uk.me.lewisdeane.ldialogs.CustomDialog;


public class DialogHelper {
    public static CustomDialog.Builder getThemedDialogBuilder(Context context, String title, String positiveText) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context, title, positiveText);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        builder.positiveColor(typedValue.data);

        return builder;
    }

    public static CustomDialog getProgressDialog(Context context) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context, "", "");
        CustomDialog dialog = builder.build();
        View view = LayoutInflater.from(context).inflate(R.layout.progress, null);
        dialog.setCustomView(view);
        dialog.setCancelable(false);
        return dialog;
    }
}
