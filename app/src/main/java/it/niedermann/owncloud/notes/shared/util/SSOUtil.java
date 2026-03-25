/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2019-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.shared.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import it.niedermann.owncloud.notes.R;

/**
 * 纯本地构建：不再通过 Nextcloud Files 应用导入账户。
 */
public class SSOUtil {

    private SSOUtil() {
        throw new UnsupportedOperationException("Do not instantiate this util class.");
    }

    public static void askForNewAccount(@NonNull Activity activity) {
        Toast.makeText(activity, R.string.local_mode_no_cloud_account, Toast.LENGTH_LONG).show();
    }

    public static boolean isConfigured(@SuppressWarnings("unused") Context context) {
        return true;
    }
}
