package io.github.loopX.XAlarm.module.UnlockTypeModule.alarmType;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import io.github.loopX.XAlarm.R;
import io.github.loopX.XAlarm.module.Alarm.AlarmNotificationManager;
import io.github.loopX.XAlarm.tools.CalculationFormula;
import io.github.loopX.XAlarm.tools.ToastMaster;
import io.github.loopX.XAlarm.view.YummyEditText;
import io.github.loopX.XAlarm.view.YummyTextView;

public class MathAlarm extends UnlockFragment {

    private int[] formula;
    private int result;
    private String input;
    private YummyTextView tvFormula;
    private YummyEditText etCalculResult;
    private OnAlarmAction mListener;
    private InputMethodManager mInputMethodManager;
    private Activity mContext;
    private Timer mTimer = null;

    public MathAlarm() {}

    public static MathAlarm newInstance() {
        return new MathAlarm();
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            mListener.closeAlarm();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_unlock_math_alarm;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mListener = (OnAlarmAction) activity;
    }

    @Override
    public void onViewInitial() {

        tvFormula = (YummyTextView) findViewById(R.id.tv_formula);
        etCalculResult = (YummyEditText) findViewById(R.id.et_calcul_result);

        etCalculResult.setFocusable(true);
        etCalculResult.setFocusableInTouchMode(true);
        etCalculResult.requestFocus();

        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        formula = CalculationFormula.generateFormula(); // Generate formula
        result = CalculationFormula.getFormulaResult(formula); // Get formula's result
        tvFormula.setText(CalculationFormula.getFormulaString(formula) + " = ?"); // Show formula on textView

        initListener();
    }

    @Override
    public void onRefreshData() {

    }


    public void initListener() {
        etCalculResult.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // If typed number is equal to result, then enable image and button
                if (!TextUtils.isEmpty(etCalculResult.getText())) {

                    input = etCalculResult.getText().toString();

                    if (input.contains("-") && input.length() > 1) {
                        if(input.startsWith("-") && !input.substring(1).contains("-")) {
                            if (Integer.parseInt(input.substring(1)) == -1 * result) {
                                success();
                            }
                        }
                    } else if (!input.contains("-")) {
                        if (Integer.parseInt(input) == result) {
                            success();
                        }
                    }
                }
            }
        });
    }

    private void success() {

        mInputMethodManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), 0);

        ToastMaster.setToast(Toast.makeText(getActivity(),
                getString(R.string.puzzle_complete),
                Toast.LENGTH_SHORT));
        ToastMaster.showToast();

        if(mTimer == null) {
            mTimer = new Timer(true);
            mTimer.schedule(task, 1200);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mInputMethodManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.closeAlarm();
        mListener = null;
    }

    @Override
    public boolean checkUnlockAlarm() {
        return false;
    }
}
