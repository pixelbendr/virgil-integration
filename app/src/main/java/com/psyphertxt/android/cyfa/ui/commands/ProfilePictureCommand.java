package com.psyphertxt.android.cyfa.ui.commands;

import com.cocosw.bottomsheet.BottomSheet;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.util.FileUtils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

public class ProfilePictureCommand implements Command {

    public void execute(final View view, final CommandListener listener) {

        new BottomSheet.Builder(view.getContext(), R.style.BottomSheet_StyleDialog)
                .title(R.string.profile_subtitle)
                .sheet(R.menu.add_profile_picture_menu)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Activity activity = (Activity) view.getContext();

                        switch (which) {
                            case R.id.text_take_photo:
                              /*  Intent intent = new Intent(view.getContext(), CameraActivity.class);
                                activity.startActivityForResult(intent, FileUtils.TAKE_PHOTO_REQUEST);*/
                                break;
                            case R.id.text_choose_photo:
                                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                choosePhotoIntent.setType("image/*");
                                activity.startActivityForResult(choosePhotoIntent, FileUtils.PICK_PHOTO_REQUEST);

                                break;
                        }

                    }
                }).show();
    }
}
