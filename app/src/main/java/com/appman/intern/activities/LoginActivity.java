package com.appman.intern.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.appman.intern.AppManHRPreferences;
import com.appman.intern.R;
import com.appman.intern.Utils;
import com.appman.intern.databinding.LoginActivityBinding;
import com.appman.intern.models.LoginModel;
import com.appman.intern.models.ResponseModel;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.regex.Pattern;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity implements Callback {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private LoginActivityBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.login_activity);

        mBinding.loginProgress.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        mBinding.passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mBinding.signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mBinding.emailInput.setText("Amorn@mail.com");
        mBinding.passwordInput.setText("12345");

        showView(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    private void checkLogin() {
        final boolean isLogin = AppManHRPreferences.isLogin(this);
        mBinding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLogin) gotoMainPage();
                else showView(true);
            }
        }, 2000);
    }

    private void showView(boolean show) {
        mBinding.emailInput.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.passwordInput.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.signInButton.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.loginProgress.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void attemptLogin() {
        // Reset errors.
        mBinding.emailInput.setError(null);
        mBinding.passwordInput.setError(null);

        // Store values at the time of the login attempt.
        String email = mBinding.emailInput.getText().toString();
        String password = mBinding.passwordInput.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mBinding.passwordInput.setError(getString(R.string.error_invalid_password));
            focusView = mBinding.passwordInput;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mBinding.emailInput.setError(getString(R.string.error_field_required));
            focusView = mBinding.emailInput;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mBinding.emailInput.setError(getString(R.string.error_invalid_email));
            focusView = mBinding.emailInput;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showView(false);
            mBinding.getRoot().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLogin();
                }
            }, 2000);
        }
    }

    private boolean isEmailValid(String email) {
        return Pattern.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+", email);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void requestLogin() {
        LoginModel login = new LoginModel();
        login.setEmail(mBinding.emailInput.getText().toString());
        login.setPassword(mBinding.passwordInput.getText().toString());

        RequestBody body = RequestBody.create(JSON, Utils.GSON.toJson(login));
        Request request = new Request.Builder()
                .url("http://hr.appmanproject.com/api/user/login")
                .post(body)
                .build();

        client.networkInterceptors().add(new StethoInterceptor());
        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(final Request request, final IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showRequestFailDialog(e.getMessage());
            }
        });
    }

    @Override
    public void onResponse(final Response response) throws IOException {
        final String body = response.body().string();
        Timber.w("body %s", body);
        final ResponseModel responseModel = Utils.GSON.fromJson(body, ResponseModel.class);
//        final ResponseModel responseModel = new ResponseModel();
//        responseModel.setStatus(200);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkResponse(responseModel);
            }
        });
    }

    private void checkResponse(ResponseModel responseModel) {
        if (responseModel.getStatus() == 200) {
            AppManHRPreferences.setLogin(this, true);
            mBinding.getRoot().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoMainPage();
                }
            }, 2000);
        } else {
            showRequestFailDialog(responseModel.getResult());
        }
    }

    private void showRequestFailDialog(String message) {
        showView(true);
        new AlertDialog.Builder(this)
                .setNegativeButton(R.string.button_ok, null)
                .setCancelable(false)
                .setMessage(message)
                .setTitle("Request fail.")
                .show();
    }

    private void gotoMainPage() {
        final Intent gotoMain = new Intent(this, MainActivity.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(gotoMain);
                finish();
            }
        });
    }
}

