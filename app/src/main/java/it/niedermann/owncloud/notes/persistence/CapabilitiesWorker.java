/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2020-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.persistence;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import it.niedermann.owncloud.notes.persistence.entity.Account;
import it.niedermann.owncloud.notes.shared.model.Capabilities;

public class CapabilitiesWorker extends Worker {

    private static final String TAG = Objects.requireNonNull(CapabilitiesWorker.class.getSimpleName());
    private static final String WORKER_TAG = "capabilities";

    private static final Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

    private static final PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(CapabilitiesWorker.class, 24, TimeUnit.HOURS)
            .setConstraints(constraints).build();

    public CapabilitiesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final var repo = NotesRepository.getInstance(getApplicationContext());
//        for (final var account : repo.getAccounts()) {
//            try {
//                final var ssoAccount = AccountImporter.getSingleSignOnAccount(getApplicationContext(), account.getAccountName());
//
//                Log.i(TAG, "Refreshing capabilities for " + ssoAccount.name);
//                final var capabilities = CapabilitiesClient.getCapabilities(getApplicationContext(), ssoAccount, account.getCapabilitiesETag(), ApiProvider.getInstance());
//                repo.updateCapabilitiesETag(account.getId(), capabilities.getETag());
//                repo.updateBrand(account.getId(), capabilities.getColor());
//                repo.updateApiVersion(account.getId(), capabilities.getApiVersion());
//                repo.updateDirectEditingAvailable(account.getId(), capabilities.isDirectEditingAvailable());
//                Log.i(TAG, capabilities.toString());
//                repo.updateDisplayName(account.getId(), CapabilitiesClient.getDisplayName(getApplicationContext(), ssoAccount, ApiProvider.getInstance()));
//            } catch (Throwable e) {
//                if (e instanceof NextcloudHttpRequestFailedException) {
//                    if (((NextcloudHttpRequestFailedException) e).getStatusCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
//                        Log.i(TAG, "Capabilities not modified.");
//                        return Result.success();
//                    } else if (((NextcloudHttpRequestFailedException) e).getStatusCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
//                        Log.i(TAG, "Server is in maintenance mode.");
//                        return Result.success();
//                    }
//                }
//                e.printStackTrace();
//                return Result.failure();
//            }
//        }
        return Result.success();
    }

    public static void update(@NonNull Context context) {
        deregister(context);
        Log.i(TAG, "Registering capabilities worker running each 24 hours.");
        WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork(WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, work);
    }

    private static void deregister(@NonNull Context context) {
        Log.i(TAG, "Deregistering all workers with tag \"" + WORKER_TAG + "\"");
        WorkManager.getInstance(context.getApplicationContext()).cancelUniqueWork(WORKER_TAG);
    }
}
