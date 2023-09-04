package com.webling.graincorp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.webling.graincorp.R;
import com.webling.graincorp.databinding.LayoutNumericKeypadBinding;

/**
 * On Screen Numeric Keypad for PIN related operations
 * Mostly from <a href="https://stackoverflow.com/a/21873135/9599716">...</a>
 *
 * @author Artaza Aziz
 */
public class NumericKeypadView extends FrameLayout {

    private final LayoutNumericKeypadBinding binding;
    int maxLength;
    String navigateBackKeyText;
    private NumericKeyPadEventListener numericKeyPadEventListener;

    public interface NumericKeyPadEventListener {
        void onNavigateBackKeyPressed();

        void onPinComplete(String pinText);

        void onPinChanged(int length);
    }

    public NumericKeypadView(Context context) {
        this(context, null);
    }

    public NumericKeypadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumericKeypadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NumericKeypadView, R.attr.numericKeypadViewStyle, 0);
        maxLength = ta.getInteger(R.styleable.NumericKeypadView_maxInputLength, 4);
        navigateBackKeyText = ta.getString(R.styleable.NumericKeypadView_navigateBackKeyText);
        ta.recycle();
        binding = LayoutNumericKeypadBinding.inflate(LayoutInflater.from(context), this, true);

        binding.numericKeypadHiddenEditText.setOnClickListener(this::onViewClicked);
        binding.numericKeypad0.setOnClickListener(this::onViewClicked);
        binding.numericKeypad1.setOnClickListener(this::onViewClicked);
        binding.numericKeypad2.setOnClickListener(this::onViewClicked);
        binding.numericKeypad3.setOnClickListener(this::onViewClicked);
        binding.numericKeypad4.setOnClickListener(this::onViewClicked);
        binding.numericKeypad5.setOnClickListener(this::onViewClicked);
        binding.numericKeypad6.setOnClickListener(this::onViewClicked);
        binding.numericKeypad7.setOnClickListener(this::onViewClicked);
        binding.numericKeypad8.setOnClickListener(this::onViewClicked);
        binding.numericKeypad9.setOnClickListener(this::onViewClicked);
        binding.numericKeypadNavigateBack.setOnClickListener(this::onViewClicked);
        binding.numericKeypadBackspace.setOnClickListener(this::onViewClicked);
    }

    public void setNumericKeyPadEventListener(NumericKeyPadEventListener numericKeyPadEventListener) {
        this.numericKeyPadEventListener = numericKeyPadEventListener;
    }

    public String getInputText() {
        return binding.numericKeypadHiddenEditText.getText().toString();
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.numeric_keypad_0 ||
                id == R.id.numeric_keypad_1 ||
                id == R.id.numeric_keypad_2 ||
                id == R.id.numeric_keypad_3 ||
                id == R.id.numeric_keypad_4 ||
                id == R.id.numeric_keypad_5 ||
                id == R.id.numeric_keypad_6 ||
                id == R.id.numeric_keypad_7 ||
                id == R.id.numeric_keypad_8 ||
                id == R.id.numeric_keypad_9) {
            binding.numericKeypadHiddenEditText.append(((TextView) view).getText());
            Editable text = binding.numericKeypadHiddenEditText.getText();
            int length = text.length();
            if (numericKeyPadEventListener != null) {
                numericKeyPadEventListener.onPinChanged(length);
                if (length >= maxLength) {
                    numericKeyPadEventListener.onPinComplete(text.toString());
                }
            }
        } else if (id == R.id.numeric_keypad_navigate_back) {
            if (numericKeyPadEventListener != null) {
                numericKeyPadEventListener.onNavigateBackKeyPressed();
            }
        } else if (id == R.id.numeric_keypad_backspace) {
            // delete one character
            Editable editable = binding.numericKeypadHiddenEditText.getText();
            int charCount = editable.length();
            if (charCount > 0) {
                editable.delete(charCount - 1, charCount);
                if (numericKeyPadEventListener != null) {
                    numericKeyPadEventListener.onPinChanged(editable.length());
                }
            }
        }
    }

    public void reset() {
        binding.numericKeypadHiddenEditText.getText().clear();
    }

    public void showBackButton() {
        TextView numericKeypadNavigateBack = binding.numericKeypadHiddenEditText;
        numericKeypadNavigateBack.setBackgroundResource(R.drawable.background_keyboard_button_auxiliary);
        numericKeypadNavigateBack.setText(navigateBackKeyText);
        numericKeypadNavigateBack.setEnabled(true);
    }
}