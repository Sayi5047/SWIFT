package iot.hustler.io.EasyDictionary.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import iot.hustler.io.EasyDictionary.R;
import iot.hustler.io.EasyDictionary.model.listeners.WebResponseListener;
import iot.hustler.io.EasyDictionary.model.pojo.RootObject;
import iot.hustler.io.EasyDictionary.utils.FontUtils;
import iot.hustler.io.EasyDictionary.utils.RestUtility;

public class SwiftSearchActivity extends Activity implements View.OnClickListener {
    private ScrollView rootView;
    LinearLayout linearLayout;
    private TextView header;
    private EditText searchView;
    private TextView dataView;
    private Button closeButton;
    private Button submitButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
        findViews();
        overridePendingTransition(R.anim.roll_up, R.anim.roll_down);
        FontUtils.findtext_and_applyTypeface(SwiftSearchActivity.this, linearLayout);
    }

    private void findViews() {
        rootView = (ScrollView) findViewById(R.id.root);
        header = (TextView) findViewById(R.id.header);
        linearLayout = (LinearLayout) findViewById(R.id.root_header);
        searchView = (EditText) findViewById(R.id.search_view);
        dataView = (TextView) findViewById(R.id.data_view);
        closeButton = (Button) findViewById(R.id.close_button);
        submitButton = (Button) findViewById(R.id.submit_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        closeButton.setOnClickListener(SwiftSearchActivity.this);
        submitButton.setOnClickListener(SwiftSearchActivity.this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-05-31 17:58:36 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == closeButton) {
            // Handle clicks for closeButton
            SwiftSearchActivity.this.finish();
        } else if (v == submitButton) {
            // Handle clicks for submitButton
            callApi();
        }
    }

    private void callApi() {
        dataView.setVisibility(View.GONE);
        dataView.setText("");
        progressBar.setVisibility(View.VISIBLE);
        new RestUtility(SwiftSearchActivity.this).getMeaning(SwiftSearchActivity.this, searchView.getText().toString(), new WebResponseListener() {
            @Override
            public void onSuccess(RootObject object) {
                progressBar.setVisibility(View.GONE);
                dataView.setVisibility(View.VISIBLE);
                if (object.getResults() != null) {
                    if (object.getResults().size() > 0) {
                        dataView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColorPrimary));
                        String pos = object.getResults().get(0).getPartOfSpeech() != null ? object.getResults().get(0).getPartOfSpeech() : "NA";
                        String ipa = object.getResults().get(0).getPronunciations() != null ? object.getResults().get(0).getPronunciations().get(0).getIpa() : "NA";
                        String definition = object.getResults().get(0).getSenses() != null ?
                                (object.getResults().get(0).getSenses().get(0).getDefinition() != null ?
                                        object.getResults().get(0).getSenses().get(0).getDefinition() != null ?
                                                object.getResults().get(0).getSenses().get(0).getDefinition().get(0)
                                                : "NA"
                                        : "NA")
                                : "NA";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            dataView.setText(String.format("%s%s%s%s%s", pos,
                                    System.lineSeparator(),
                                    System.lineSeparator(),
                                    ipa,
                                    System.lineSeparator(),
                                    System.lineSeparator(),
                                    definition));
                        } else {
                            dataView.setText(String.format("%s\n\n%s\n\n%s", pos, ipa, definition));
                        }
                    } else {
                        dataView.setTextColor(Color.RED);
                        dataView.setText(getString(R.string.no_word_found));
                    }
                }

            }

            @Override
            public void onError(String errormessage) {
                progressBar.setVisibility(View.GONE);
                dataView.setVisibility(View.VISIBLE);
                dataView.setText(errormessage);
                dataView.setTextColor(Color.RED);

            }
        });
    }
}
