/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
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
package com.wemakestuff.mmsplus.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.wemakestuff.mmsplus.MmsApp;
import com.wemakestuff.mmsplus.R;
import com.wemakestuff.mmsplus.model.Telephony.Mms;
import com.wemakestuff.mmsplus.model.Telephony.Mms.Addr;
import com.wemakestuff.mmsplus.pdu.EncodedStringValue;
import com.wemakestuff.mmsplus.pdu.PduHeaders;
import com.wemakestuff.mmsplus.pdu.PduPersister;

public class AddressUtils {
    private static final String TAG = "AddressUtils";
    private static PhoneNumberUtil mPhoneNumberUtil;

    private AddressUtils() {
        // Forbidden being instantiated.
    }

    public static String getFrom(Context context, Uri uri) {
        String msgId = uri.getLastPathSegment();
        Uri.Builder builder = Mms.CONTENT_URI.buildUpon();

        builder.appendPath(msgId).appendPath("addr");

        Cursor cursor = SqliteWrapper.query(context, context.getContentResolver(),
                            builder.build(), new String[] {Addr.ADDRESS, Addr.CHARSET},
                            Addr.TYPE + "=" + PduHeaders.FROM, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String from = cursor.getString(0);

                    if (!TextUtils.isEmpty(from)) {
                        byte[] bytes = PduPersister.getBytes(from);
                        int charset = cursor.getInt(1);
                        return new EncodedStringValue(charset, bytes)
                                .getString();
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return context.getString(R.string.hidden_sender_address);
    }

    /**
     * isPossiblePhoneNumberCanDoFileAccess does a more accurate test if the input is a
     * phone number, but it can do file access to load country prefixes and other info, so
     * it's not safe to call from the UI thread.
     * @param query the phone number to test
     * @return true if query looks like a valid phone number
     */
    public static boolean isPossiblePhoneNumberCanDoFileAccess(String query) {
        String currentCountry = MmsApp.getApplication().getCurrentCountryIso().toUpperCase();
        if (mPhoneNumberUtil == null) {
            mPhoneNumberUtil = PhoneNumberUtil.getInstance();
        }
        return mPhoneNumberUtil.isPossibleNumber(query, currentCountry);
    }
}
