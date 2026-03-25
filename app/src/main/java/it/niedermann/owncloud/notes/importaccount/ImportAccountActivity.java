/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2020-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.importaccount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nextcloud.android.common.ui.util.extensions.AppCompatActivityExtensionsKt;

import it.niedermann.owncloud.notes.R;
import it.niedermann.owncloud.notes.exception.ExceptionHandler;
import it.niedermann.owncloud.notes.main.MainActivity;

/**
 * 纯本地模式：不再从 Nextcloud Files 导入账户，跳转主界面。
 */
public class ImportAccountActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_IMPORT_ACCOUNT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatActivityExtensionsKt.adjustUIForAPILevel35(this);
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));
        Toast.makeText(this, R.string.local_mode_no_cloud_account, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
