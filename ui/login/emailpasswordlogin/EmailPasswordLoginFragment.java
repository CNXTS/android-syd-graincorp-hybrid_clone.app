package com.webling.graincorp.ui.login.emailpasswordlogin;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.webling.graincorp.GrainCorpApp;
import com.webling.graincorp.R;
import com.webling.graincorp.databinding.FragmentEmailPasswordLoginBinding;
import com.webling.graincorp.ui.listeners.OnFragmentInteractionListener;
import com.webling.graincorp.ui.login.DaggerLoginComponent;
import com.webling.graincorp.ui.login.OnLoginInteractionListener;
import com.webling.graincorp.util.AnimUtil;
import com.webling.graincorp.util.Util;
import com.webling.graincorp.widget.ClearableEditText;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordLoginFragment extends Fragment implements EmailPasswordLoginContract.View {

    public static final String EXTRA_MAX_PIN_FAIL_ATTEMPTS_ERROR = "EXTRA_MAX_PIN_FAIL_ATTEMPTS_ERROR";
    public static final String EXTRA_PASSWORD_CHANGED_ERROR = "EXTRA_PASSWORD_CHANGED_ERROR";

    @Inject
    EmailPasswordLoginPresenter presenter;

    private FragmentEmailPasswordLoginBinding binding;
    private ClearableEditText editEmail;
    private ClearableEditText editPassword;
    private TextView loginErrorMessage;
    private Button loginSignInButton;
    private LinearLayout emailLoginForm;
    private ProgressBar loginProgress;

    private OnFragmentInteractionListener fragmentInteractionListener;
    private OnLoginInteractionListener loginInteractionListener;

    public static EmailPasswordLoginFragment newInstance() {
        return new EmailPasswordLoginFragment();
    }

    public static EmailPasswordLoginFragment newInstanceShowMaxPinFailAttemptsError() {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_MAX_PIN_FAIL_ATTEMPTS_ERROR, true);
        EmailPasswordLoginFragment fragment = new EmailPasswordLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EmailPasswordLoginFragment newInstanceShowPasswordChangedError() {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_PASSWORD_CHANGED_ERROR, true);
        EmailPasswordLoginFragment fragment = new EmailPasswordLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EmailPasswordLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        DaggerLoginComponent.builder()
                .grainCorpAppComponent(((GrainCorpApp) requireActivity().getApplication()).getComponent())
                .emailPasswordLoginModule(new EmailPasswordLoginModule(this))
                .build()
                .inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEmailPasswordLoginBinding.inflate(inflater, container, false);
        initViews();
        return binding.getRoot();
    }

    private void initViews() {
        editEmail = binding.loginEditEmail;
        editPassword = binding.loginEditPassword;
        loginErrorMessage = binding.loginErrorMessage;
        loginSignInButton = binding.loginSignInButton;
        emailLoginForm = binding.emailLoginForm;
        loginProgress = binding.loginProgress;

        loginSignInButton.setOnClickListener(view -> signInButtonClicked());
        binding.registerButton.setOnClickListener(view -> registerButtonPressed());
        binding.forgotPasswordButton.setOnClickListener(view -> forgotPasswordButtonPressed());
        binding.loginEditEmail.addTextChangedListener(textWatcher);
        binding.loginEditPassword.addTextChangedListener(textWatcher);
        binding.loginEditPassword.setOnEditorActionListener(this::passwordViewOnEditorAction);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            textChanged();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.setTitle(getString(R.string.title_login));
            fragmentInteractionListener.setCurrentlySelectedFragment(this);
        }
        presenter.subscribe();

        Bundle b = getArguments();
        if (b != null) {
            presenter.onShowMaxPinFailAttemptsError(b.getBoolean(EXTRA_MAX_PIN_FAIL_ATTEMPTS_ERROR));
            presenter.onPasswordChangedError(b.getBoolean(EXTRA_PASSWORD_CHANGED_ERROR));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            fragmentInteractionListener = (OnFragmentInteractionListener) context;
        }

        if (context instanceof OnLoginInteractionListener) {
            loginInteractionListener = (OnLoginInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
        loginInteractionListener = null;
    }

    @Override
    public void onDestroyView() {
        presenter.unsubscribe();
        super.onDestroyView();
    }

    private void signInButtonClicked() {
        Util.hideSoftKeyboard(getActivity());
        if (editEmail.getText() != null && editPassword.getText() != null) {
            presenter.onSignIn(editEmail.getText().toString(), editPassword.getText().toString());
        }
    }

    private void registerButtonPressed() {
        loginInteractionListener.registerButtonPressed();
    }

    private void forgotPasswordButtonPressed() {
        loginInteractionListener.forgotPasswordButtonPressed();
    }

    private void textChanged() {
        hideError();
        if (editEmail.getText() != null && editPassword.getText() != null) {
            loginSignInButton.setEnabled(!editEmail.getText().toString().isEmpty() && !editPassword.getText().toString().isEmpty());
        }
    }

    boolean passwordViewOnEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
            signInButtonClicked();
            return true;
        }
        return false;
    }

    @Override
    public void showProgress(final boolean show) {
        AnimUtil.showProgress(show, emailLoginForm, loginProgress, View.INVISIBLE);
    }

    @Override
    public void showInvalidDetailsError() {
        requireActivity().runOnUiThread(() -> {
            showProgress(false);
            loginErrorMessage.setText(getString(R.string.login_modal_invalid_details_error));
            loginErrorMessage.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void showMaxPinFailAttemptsError() {
        requireActivity().runOnUiThread(() -> {
            showProgress(false);
            loginErrorMessage.setText(getString(R.string.login_modal_max_pin_fail_attempts_error));
            loginErrorMessage.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void showPasswordChangedError() {
        requireActivity().runOnUiThread(() -> {
            showProgress(false);
            loginErrorMessage.setText(getString(R.string.login_modal_password_changed_error));
            loginErrorMessage.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void hideError() {
        requireActivity().runOnUiThread(() -> loginErrorMessage.setVisibility(View.GONE));
    }

    @Override
    public void setEmailViewText(String savedEmail) {
        editEmail.setText(savedEmail);
    }

    @Override
    public void login(String email, String password) {
        if (loginInteractionListener != null) {
            showProgress(true);
            loginInteractionListener.loginWithEmailPassword(email, password);
        }
    }
}