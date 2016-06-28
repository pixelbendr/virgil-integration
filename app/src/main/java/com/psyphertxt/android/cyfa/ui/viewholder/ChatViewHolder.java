package com.psyphertxt.android.cyfa.ui.viewholder;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.model.Chat.Direction;
import com.psyphertxt.android.cyfa.model.ContentType;
import com.psyphertxt.android.cyfa.model.DeliveryStatus;
import com.psyphertxt.android.cyfa.model.Themes;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;
import com.psyphertxt.android.cyfa.ui.widget.Shape;
import com.psyphertxt.android.cyfa.util.Testable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cyd.awesome.material.AwesomeText;
import cyd.awesome.material.FontCharacterMaps;

/**
 * View holder for reading and sending messages
 */

public class ChatViewHolder extends AbstractSwipeableItemViewHolder implements View.OnClickListener {

    @InjectView(android.R.id.title)
    TextView txtName;

    @InjectView(android.R.id.summary)
    TextView txtMessage;

    @InjectView(R.id.timestamp)
    TextView txtTimeStamp;

    @InjectView(R.id.line_divider)
    FrameLayout viewDivider;

    @InjectView(R.id.text_message_status)
    AwesomeText txtMessageStatus;

    @InjectView(R.id.text_message_read_status)
    AwesomeText txtMessageReadStatus;

    @InjectView(R.id.chat_layout)
    RelativeLayout chatLayout;

    @InjectView(R.id.media_layout_scroll_view)
    HorizontalScrollView mediaLayoutScrollView;

    @InjectView(R.id.media_layout)
    LinearLayout mediaLayout;

    @InjectView(R.id.linear_layout)
    LinearLayout linearLayout;

    private Context context;
    private Settings settings;
    private Chat chat;

    public ChatViewHolder(View itemView) {

        super(itemView);
        ButterKnife.inject(this, itemView);
        itemView.setOnClickListener(this);
        this.context = itemView.getContext();

        settings = new Settings(context);
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {

        this.chat = chat;

        float alphaBefore = 1f;
        float alphaAfter = 0.5f;

        linearLayout.setAlpha(alphaBefore);
        txtName.setAlpha(alphaBefore);
        viewDivider.setAlpha(alphaBefore);

        int readMessageColor = ContextCompat.getColor(context, R.color.md_teal_400);
        int editedMessageColor = ContextCompat.getColor(context, R.color.selective_yellow);
        int messageColor = ContextCompat.getColor(context, R.color.dove_gray);
        int unreadMessageColor = ContextCompat.getColor(context, R.color.silver_chalice);

        int color = Themes.getThemeByColor(settings.getTheme()).get(Config.KEY_COLOR_PRIMARY);
        int colorPrimary = ContextCompat.getColor(context, color);
        int incomingTimerColor = ContextCompat.getColor(context, R.color.md_deep_purple_500);
        int outgoingTimerColor = ContextCompat.getColor(context, R.color.md_deep_purple_200);

        txtMessageStatus.setVisibility(View.VISIBLE);
        txtMessageReadStatus.setVisibility(View.GONE);
        mediaLayoutScrollView.setVisibility(View.GONE);

        Shape.background(viewDivider, Shape.rectangle(Color.WHITE));

        txtName.setTextColor(readMessageColor);
        txtMessage.setTextColor(messageColor);
        txtMessageStatus.setTextColor(unreadMessageColor);
        txtMessageStatus.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_CHECK);

        //TODO investigate alternative solution for memory enhancement
        RelativeLayout.LayoutParams viewDividerLayoutParams = (RelativeLayout.LayoutParams) viewDivider.getLayoutParams();
        RelativeLayout.LayoutParams linearLayoutLayoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        RelativeLayout.LayoutParams txtNameLayoutParams = (RelativeLayout.LayoutParams) txtName.getLayoutParams();

        viewDividerLayoutParams.setMargins(0, 0, 0, 0);
        linearLayoutLayoutParams.setMargins(0, 0, 0, 0);
        txtNameLayoutParams.setMargins(32, 0, 0, 0);

        switch (chat.getMessageType()) {

            case Chat.MessageType.PRIVATE:
                txtMessage.setTextColor(Color.BLACK);
                txtMessageStatus.setVisibility(View.GONE);
                // txtMessageStatus.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_LOCK);
                Shape.background(viewDivider, Shape.rectangle(Color.BLACK));
                viewDividerLayoutParams.setMargins(33, 0, 0, 0);
                linearLayoutLayoutParams.setMargins(21, 0, 0, 0);
                txtNameLayoutParams.setMargins(52, 0, 0, 0);
                break;

            case Chat.MessageType.REGULAR:
                //if user is in private mode
                //do some UI enhancement to make the UX obvious
                if (Message.IS_PRIVATE) {
                    linearLayout.setAlpha(alphaAfter);
                    txtName.setAlpha(alphaAfter);
                    viewDivider.setAlpha(alphaAfter);
                }
        }

        if (chat.getDirection() == Direction.INCOMING) {

            switch (chat.getMessageType()) {

                case Chat.MessageType.PRIVATE:
                    txtName.setTextColor(colorPrimary);
                    break;

                case Chat.MessageType.REGULAR:
                    txtMessageStatus.setVisibility(View.GONE);
                    Shape.background(viewDivider, Shape.rectangle(colorPrimary));
                    txtName.setTextColor(colorPrimary);
                    break;
            }

        } else {

            Shape.background(viewDivider, Shape.rectangle(Color.WHITE));

        }

        if (chat.getDeliveryStatus() == DeliveryStatus.DELETING) {
            int deletedColor = ContextCompat.getColor(context, R.color.silver);
            int deletingColor = ContextCompat.getColor(context, R.color.cinnabar);
            Shape.background(viewDivider, Shape.rectangle(deletingColor));
            txtMessage.setTextColor(deletedColor);
        }

        if (chat.getDeliveryStatus() == DeliveryStatus.DELETED) {
            int deletedColor = ContextCompat.getColor(context, R.color.silver);
            Shape.background(viewDivider, Shape.rectangle(deletedColor));
            txtMessage.setTextColor(deletedColor);
            txtMessageStatus.setVisibility(View.GONE);
        }

        if (chat.getDeliveryStatus() == DeliveryStatus.EDITING) {
            int editingColor = ContextCompat.getColor(context, R.color.md_amber_300);
            Shape.background(viewDivider, Shape.rectangle(editingColor));
        }

        //check if the chat message has been edited, update the UI to reflect that
        if (chat.getDirection() == Direction.INCOMING &&
                chat.getDeliveryStatus() == DeliveryStatus.EDITED) {
            txtMessageStatus.setTextColor(editedMessageColor);
            txtMessageStatus.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_MODE_EDIT);
            txtMessageStatus.setVisibility(View.VISIBLE);
            if (Message.IS_PRIVATE) {
                Shape.background(viewDivider, Shape.rectangle(Color.BLACK));
            } else {
                Shape.background(viewDivider, Shape.rectangle(colorPrimary));
            }
        }

        //check if the chat message has been read or edited and updated, the UI should reflect that too
        if (chat.getDirection() == Direction.OUTGOING &&
                chat.getDeliveryStatus() == DeliveryStatus.READ ||
                chat.getDirection() == Direction.OUTGOING &&
                        chat.getDeliveryStatus() == DeliveryStatus.EDITED) {

            txtMessageStatus.setTextColor(readMessageColor);
            txtMessageStatus.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_CHECK);
            txtMessageReadStatus.setVisibility(View.VISIBLE);

        }

        if (chat.getTimer() != null) {

            if (chat.getTimer() != Config.NUMBER_ZERO) {

                if (chat.getDirection() == Direction.INCOMING) {

                   /* txtName.setTextColor(incomingTimerColor);
                    Shape.background(viewDivider, Shape.rectangle(incomingTimerColor));*/
                    txtMessageStatus.setVisibility(View.VISIBLE);
                    txtMessageStatus.setTextColor(incomingTimerColor);
                    txtMessageStatus.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_TIMER);

                } else {

                    txtMessageStatus.setVisibility(View.VISIBLE);
                    txtMessageStatus.setTextColor(outgoingTimerColor);
                    txtMessageStatus.setMaterialDesignIcon(FontCharacterMaps.MaterialDesign.MD_TIMER);

                }
            }
        }

        txtName.setText(chat.getName());
        txtMessage.setText(chat.getText());
        txtTimeStamp.setText(chat.getDeliveredAt());

        //=============== Media ==============//

        if (chat.getContentType() != ContentType.TEXT) {

            mediaLayoutScrollView.setVisibility(View.VISIBLE);

            getMediaContent(chat, new CallbackListener.completion() {
                @Override
                public void done() {


                }
            });

           /* Picasso.with(imageView.getContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.photo)
                    .fit()
                    .centerCrop()
                    .into(imageView);*/
        }

        //=============== Media ==============//


    }

    private void getMediaContent(Chat chat, CallbackListener.completion completion) {

        //lets get contents from the chat object
        Map<String, Object> contents = chat.getContent();

        for (Object content : contents.entrySet()) {

            //lets retrieve the entry of this chat object
            //this will always be just one object
            //which means the loop runs just once
            Map.Entry contentEntry = (Map.Entry) content;
            String contentKey = (String) contentEntry.getKey();

            //the value contants a map of multiple media types
            Map<String, Object> contentValue = (HashMap<String, Object>) contentEntry.getValue();

            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alerts.show(v.getContext(), "Full Image", "This will show the full image with sharing options. Please expect this in the next beta version.");
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout
                    .LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 0, 9, 0);
            imageView.setLayoutParams(layoutParams);

            List<Drawable> list = new ArrayList<>();
            list.add(ContextCompat.getDrawable(context, R.drawable.ic_image_view_1));
            list.add(ContextCompat.getDrawable(context, R.drawable.ic_image_view_2));
            imageView.setImageDrawable(list.get(new Random().nextInt(list.size())));
            //  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150, 1.0f);
            // layoutParams.setMargins(5, 5, 5, 5);

            mediaLayout.addView(imageView);

            new Testable.Spec()
                    .describe("content value")
                    .expect(contentValue.entrySet())
                    .run();

            //lets loop through and retrieve each media type
            for (Object mediaObject : contentValue.entrySet()) {

                Map.Entry mediaEntry = (Map.Entry) mediaObject;

                String mediaKey = (String) mediaEntry.getKey();
                //   Map<String, Object> mediaValue = (HashMap<String, Object>) mediaEntry.getValue();


            }
        }
        completion.done();
    }

    private void getMedia(Chat chat, CallbackListener.completion completion) {

        Map<String, Object> root = chat.getContent();
        Set<String> keys = root.keySet();
        for (String key : keys) {

//            new Testable.Spec()
//                    .describe("CURRENT KEY")
//                    .expect(key)
//                    .run();

            Map<String, Object> subRoot = (Map<String, Object>) root.get(key);
            Set<String> subKeys = subRoot.keySet();
            for (String subkey : subKeys) {
                Log.i("DATA : ", String.format("%s => %s", subkey, subRoot.get(subkey).toString()));

                new Testable.Spec()
                        .describe(String.format("VALUE FOR %s ", subkey))
                        .expect(subRoot.get(subkey).toString())
                        .run();
            }
        }

        //lets get contents from the chat object
//        Map<String, Object> contents = chat.getContent();

        //string dkfgjhkdfhgkhdfgh
        //hashmap
        // Media
        // Media
        // Media


//        new Testable.Spec()
//                .describe("the content contained in the value")
//                .expect(contents)
//                .run();

        /*for (Object content : contents.entrySet()) {

            //lets retrieve the entry of this chat object
            //this will always be just one object
            //which means the loop runs just once
            Map.Entry contentEntry = (Map.Entry) content;
            String contentKey = (String) contentEntry.getKey();

            //the value contants a map of multiple media types
            Map<String, Object> contentValue = (HashMap<String, Object>) contentEntry.getValue();

            new Testable.Spec()
                    .describe("content value")
                    .expect(contentValue.entrySet())
                    .run();

            //lets loop through and retrieve each media type
            for (Object mediaObject : contentValue.entrySet()) {

                Map.Entry mediaEntry = (Map.Entry) mediaObject;

                String mediaKey = (String) mediaEntry.getKey();
                //   Map<String, Object> mediaValue = (HashMap<String, Object>) mediaEntry.getValue();

                new Testable.Spec()
                        .describe("content value in loop two")
                        .expect(mediaKey)
                        .run();

            }
        }*/
        completion.done();
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public View getSwipeableContainerView() {
        return chatLayout;
    }
}