/*
 * Nextcloud Notes - Android Client
 *
 * SPDX-FileCopyrightText: 2020-2024 Nextcloud GmbH and Nextcloud contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package it.niedermann.owncloud.notes.manageaccounts;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

import it.niedermann.owncloud.notes.LockedActivity;
import it.niedermann.owncloud.notes.NotesApplication;
import it.niedermann.owncloud.notes.R;
import it.niedermann.owncloud.notes.branding.BrandingUtil;
import it.niedermann.owncloud.notes.branding.DeleteAlertDialogBuilder;
import it.niedermann.owncloud.notes.databinding.ActivityManageAccountsBinding;
import it.niedermann.owncloud.notes.exception.ExceptionDialogFragment;
import it.niedermann.owncloud.notes.persistence.NotesRepository;
import it.niedermann.owncloud.notes.persistence.entity.Account;
import it.niedermann.owncloud.notes.shared.model.IResponseCallback;
import it.niedermann.owncloud.notes.shared.model.NotesSettings;
public class ManageAccountsActivity extends LockedActivity implements IManageAccountsCallback {

    private ActivityManageAccountsBinding binding;
    private ManageAccountsViewModel viewModel;
    private ManageAccountAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityManageAccountsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(ManageAccountsViewModel.class);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        adapter = new ManageAccountAdapter(this);
        binding.accounts.setAdapter(adapter);

        viewModel.getAccounts$().observe(this, (accounts) -> {
            if (accounts == null || accounts.size() < 1) {
                finish();
                return;
            }
            this.adapter.setLocalAccounts(accounts);
            viewModel.getCurrentAccount(this, new IResponseCallback<>() {
                @Override
                public void onSuccess(Account result) {
                    runOnUiThread(() -> adapter.setCurrentLocalAccount(result));
                }

                @Override
                public void onError(@NonNull Throwable t) {
                    runOnUiThread(() -> adapter.setCurrentLocalAccount(null));
                    t.printStackTrace();
                }
            });
        });
    }

    public void onSelect(@NonNull Account accountToSelect) {
        adapter.setCurrentLocalAccount(accountToSelect);
        viewModel.selectAccount(accountToSelect, this);
    }

    public void onDelete(@NonNull Account accountToDelete) {
        viewModel.countUnsynchronizedNotes(accountToDelete.getId(), new IResponseCallback<>() {
            @Override
            public void onSuccess(Long unsynchronizedChangesCount) {
                runOnUiThread(() -> {
                    if (unsynchronizedChangesCount > 0) {
                        showRemoveAccountAlertDialog(accountToDelete, unsynchronizedChangesCount);
                    } else {
                        viewModel.deleteAccount(accountToDelete, ManageAccountsActivity.this);
                    }
                });
            }

            @Override
            public void onError(@NonNull Throwable t) {
                ExceptionDialogFragment.newInstance(t).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
            }
        });
    }

    private void showRemoveAccountAlertDialog(@NonNull Account accountToDelete, Long unsynchronizedChangesCount) {
        final MaterialAlertDialogBuilder alertDialogBuilder = new DeleteAlertDialogBuilder(ManageAccountsActivity.this)
                .setTitle(getString(R.string.remove_account, accountToDelete.getUserName()))
                .setMessage(getResources().getQuantityString(R.plurals.remove_account_message, (int) unsynchronizedChangesCount.longValue(), accountToDelete.getAccountName(), unsynchronizedChangesCount))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.simple_remove, (d, l) -> viewModel.deleteAccount(accountToDelete, ManageAccountsActivity.this));

        NotesApplication.brandingUtil().dialog.colorMaterialAlertDialogBackground(this, alertDialogBuilder);

        alertDialogBuilder.show();
    }

    public void onChangeNotesPath(@NonNull Account localAccount) {
        changeAccountSetting(localAccount,
                R.string.settings_notes_path,
                R.string.settings_notes_path_description,
                R.string.settings_notes_path_success,
                NotesSettings::getNotesPath,
                property -> new NotesSettings(property, null)
        );
    }

    public void onChangeFileSuffix(@NonNull Account localAccount) {
        changeAccountSetting(localAccount,
                R.string.settings_file_suffix,
                R.string.settings_file_suffix_description,
                R.string.settings_file_suffix_success,
                NotesSettings::getFileSuffix,
                property -> new NotesSettings(null, property)
        );
    }

    @SuppressWarnings("unused")
    private void changeAccountSetting(@NonNull Account localAccount, @StringRes int title, @StringRes int message, @StringRes int successMessage, @NonNull Function<NotesSettings, String> propertyExtractor, @NonNull Function<String, NotesSettings> settingsFactory) {
        Toast.makeText(this, R.string.local_mode_no_cloud_account, Toast.LENGTH_LONG).show();
    }

    @Override
    public void applyBrand(int color) {
        final var util = BrandingUtil.of(color, this);
        util.platform.themeStatusBar(this);
        util.material.themeToolbar(binding.toolbar);
    }
}
