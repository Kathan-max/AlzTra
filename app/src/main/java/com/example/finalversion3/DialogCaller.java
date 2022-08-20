package com.example.finalversion3;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.ValueEventListener;

public class DialogCaller {
    public static void showDialog(Context context, String title, String message,
                                  DialogInterface.OnClickListener onClickListener) {

        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder((Context) context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setPositiveButton("Ok",onClickListener);
            dialog.setNegativeButton("Cancel",null);
            dialog.show();
        }catch (Exception e){
//            Toast.makeText(context, "Open Dashboard now", Toast.LENGTH_SHORT).show();
        }
    }
}
