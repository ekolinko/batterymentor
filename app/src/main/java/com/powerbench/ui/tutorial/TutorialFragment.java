package com.powerbench.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.powerbench.R;
import com.powerbench.ui.common.CommonFragment;
import com.powerbench.ui.theme.Theme;

/**
 * Tutorial fragment that shows information about the app.
 */
public class TutorialFragment extends CommonFragment {

    /**
     * The tutorial title resource id.
     */
    private int mTutorialTitleResourceId;

    /**
     * The tutorial image drawable resource id.
     */
    private int mTutorialImageResourceId;

    /**
     * The tutorial text resource id.
     */
    private int mTutorialTextResourceId;

    /**
     * The current theme.
     */
    private Theme mTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        if (mTheme != null) {
            applyTheme(mTheme);
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTutorialTitleResourceId = arguments.getInt(getString(R.string.argument_tutorial_title_res_id));
            mTutorialImageResourceId = arguments.getInt(getString(R.string.argument_tutorial_image_res_id));
            mTutorialTextResourceId = arguments.getInt(getString(R.string.argument_tutorial_text_res_id));
        }

        TextView titleView = (TextView) view.findViewById(R.id.powerbench_tutorial_title);
        if (titleView != null) {
            titleView.setText(mTutorialTitleResourceId);
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.powerbench_tutorial_image);
        if (imageView != null) {
            imageView.setImageResource(mTutorialImageResourceId);
        }

        TextView textView = (TextView) view.findViewById(R.id.powerbench_tutorial_text);
        if (textView != null) {
            textView.setText(mTutorialTextResourceId);
        }

        setRetainInstance(true);
        return view;
    }
}
