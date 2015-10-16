/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.cardemulation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * public class CardEmulationFragment (extends Fragment)
 *
 * This class is a simple UI class that allows the app to separate the Loyalty Card emulation component
 * from the logging window using an inflater.
 *
 *      Classes:
 *              private class AccountUpdater (implements TextWatcher)
 *              private class UsernameUpdater (implements TextWatcher)
 *      Methods:
 *              public void onCreate(Bundle savedInstanceState)
 *              public View onCreateView(LayoutInflater inflater, ViewGroup container,
 *                      Bundle savedInstanceState)
 *
 *      Author: Android Developers
 *      Modified by: David Tran
 *      Modified on: March 9, 2015
 *
 */
public class CardEmulationFragment extends Fragment {

    public static final String TAG = "CardEmulationFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * public View onCreateView
     *
     * Creates the view and manages the EditTexts for each of the fields used in this app.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_fragment, container, false);

        EditText accountNumber = (EditText) v.findViewById(R.id.card_account_field);
        accountNumber.setText(AccountStorage.GetAccount(getActivity()));
        accountNumber.addTextChangedListener(new AccountUpdater());

        EditText accountUsername = (EditText) v.findViewById(R.id.card_username_field);
        accountUsername.setText(AccountStorage.GetUsername(getActivity()));
        accountUsername.addTextChangedListener(new UsernameUpdater());

        return v;
    }

    /**
     * private class AccountUpdater (implements TextWatcher)
     *
     * This class watches the text to change in the EditText components of the View. Upon change,
     * the class updates the static AccountStorage class to store the values that have been changed.
     *
     *      Methods:
     *              public void beforeTextChanged(CharSequence s, int start, int count, int after)
     *              public void onTextChanged(CharSequence s, int start, int before, int count)
     *              public void afterTextChanged(Editable s)
     *
     *      Created by: Android Developers
     *
     */
    private class AccountUpdater implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not implemented.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not implemented.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String account = s.toString();
            AccountStorage.SetAccount(getActivity(), account);
        }
    }

    /**
     * private class UsernameUpdater (implements TextWatcher)
     *
     * This class watches the text to change in the EditText components of the View. Upon change,
     * the class updates the static AccountStorage class to store the values that have been changed.
     *
     *      Methods:
     *              public void beforeTextChanged(CharSequence s, int start, int count, int after)
     *              public void onTextChanged(CharSequence s, int start, int before, int count)
     *              public void afterTextChanged(Editable s)
     *
     *      Created by: David Tran
     *      Created on: March 9, 2015
     *
     */
    private class UsernameUpdater implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not implemented.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not implemented.
        }

        @Override
        public void afterTextChanged(Editable s) {
            String username = s.toString();
            AccountStorage.SetUsername(getActivity(), username);
        }
    }
}
