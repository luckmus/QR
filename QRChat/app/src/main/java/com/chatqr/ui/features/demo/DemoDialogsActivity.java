package com.chatqr.ui.features.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatqr.Controller;
import com.chatqr.R;
import com.chatqr.bl.dao.model.Chat;
import com.chatqr.utils.AppUtils;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

/*
 * Created by troy379 on 05.04.17.
 */
public abstract class DemoDialogsActivity extends AppCompatActivity
        implements DialogsListAdapter.OnDialogClickListener<Chat>,
        DialogsListAdapter.OnDialogLongClickListener<Chat> {

    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Chat> dialogsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.with(DemoDialogsActivity.this).load(url).into(imageView);
            }
        };
    }

    @Override
    public void onDialogLongClick(Chat dialog) {
        AppUtils.showToast(
                this,
                dialog.getDialogName(),
                false);
    }

    @Override
    public void onBackPressed() {

    }

    public void addClick(final View view) {
        Toast.makeText(this,"New", Toast.LENGTH_SHORT).show();
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.key_dlg, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        final EditText chatName = (EditText) promptsView.findViewById(R.id.chat_name);
        final EditText keyVal = (EditText) promptsView.findViewById(R.id.key_value);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.rsc_ok_btn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //Вводим текст и отображаем в строке ввода на основном экране:
                                createChat(view, chatName.getText().toString(), keyVal.getText().toString());
                            }
                        })
                .setNegativeButton(R.string.rsc_cancel_btn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        //Создаем AlertDialog:
        AlertDialog alertDialog = mDialogBuilder.create();

        //и отображаем его:
        alertDialog.show();
    }
    public void createChat(View view, String name, String base) {
        Controller.getInstance().createChat(this, name, base);
    }
}
