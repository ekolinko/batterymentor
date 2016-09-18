package com.batterymentor.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.batterymentor.R;
import com.batterymentor.ui.common.CommonFragment;
import com.batterymentor.ui.theme.Theme;

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

    /**
     * The title view.
     */
    private TextView mTitleView;

    /**
     * The image view
     */
    private ImageView mImageView;

    /**
     * The text view.
     */
    private TextView mTextView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTutorialTitleResourceId = arguments.getInt(getString(R.string.argument_tutorial_title_res_id));
            mTutorialImageResourceId = arguments.getInt(getString(R.string.argument_tutorial_image_res_id));
            mTutorialTextResourceId = arguments.getInt(getString(R.string.argument_tutorial_text_res_id));
        }

        mTitleView = (TextView) view.findViewById(R.id.powerbench_tutorial_title);
        if (mTitleView != null) {
            mTitleView.setText(mTutorialTitleResourceId);
        }

        mImageView = (ImageView) view.findViewById(R.id.powerbench_tutorial_image);
        if (mImageView != null) {
            mImageView.setImageResource(mTutorialImageResourceId);
        }

        mTextView = (TextView) view.findViewById(R.id.powerbench_tutorial_text);
        if (mTextView != null) {
            mTextView.setText(mTutorialTextResourceId);
        }

        if (mTheme != null) {
            applyTheme(mTheme);
        }

        setRetainInstance(true);
        return view;
    }

    /**
     * Apply the specified theme to this fragment.
     *
     * @param theme the theme to apply to this fragment.
     */
    @Override
    public void applyTheme(Theme theme) {
//        mTheme = theme;
//        if (mTitleView != null) {
//            mTitleView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
//        }
//        if (mTextView != null) {
//            mTextView.setTextColor(ContextCompat.getColor(getContext(), theme.getColorResource()));
//        }
    }
}
