package com.wemakestuff.mmsplus.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.wemakestuff.mmsplus.R;
import com.wemakestuff.mmsplus.data.Contact;
import com.wemakestuff.mmsplus.data.Conversation;
import com.wemakestuff.mmsplus.data.RecipientIdCache;

public class LogTag {
	public static final String TAG = "Mms";

	public static final String TRANSACTION = "Mms:transaction";
	public static final String APP = "Mms:app";
	public static final String THREAD_CACHE = "Mms:threadcache";
	public static final String THUMBNAIL_CACHE = "Mms:thumbnailcache";
	public static final String PDU_CACHE = "Mms:pducache";
	public static final String WIDGET = "Mms:widget";
	public static final String CONTACT = "Mms:contact";

	/**
	 * Log tag for enabling/disabling StrictMode violation log. To enable: adb
	 * shell setprop log.tag.Mms:strictmode DEBUG
	 */
	public static final String STRICT_MODE_TAG = "Mms:strictmode";
	public static final boolean VERBOSE = false;
	public static final boolean SEVERE_WARNING = true; // Leave this true
	private static final boolean SHOW_SEVERE_WARNING_DIALOG = false; // Set to
																		// false
																		// before
																		// ship
	public static final boolean DEBUG_SEND = false; // Set to false before ship
	public static final boolean DEBUG_DUMP = false; // Set to false before ship
	public static final boolean ALLOW_DUMP_IN_LOGS = false; // Set to false
															// before ship

	private static String prettyArray(String[] array) {
		if (array.length == 0) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder("[");
		int len = array.length - 1;
		for (int i = 0; i < len; i++) {
			sb.append(array[i]);
			sb.append(", ");
		}
		sb.append(array[len]);
		sb.append("]");

		return sb.toString();
	}

	private static String logFormat(String format, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof String[]) {
				args[i] = prettyArray((String[]) args[i]);
			}
		}
		String s = String.format(format, args);
		s = "[" + Thread.currentThread().getId() + "] " + s;
		return s;
	}

	public static void debug(String format, Object... args) {
		Log.d(TAG, logFormat(format, args));
	}

	public static void warn(String format, Object... args) {
		Log.w(TAG, logFormat(format, args));
	}

	public static void error(String format, Object... args) {
		Log.e(TAG, logFormat(format, args));
	}

	public static void dumpInternalTables(final Context context) {
		if (!ALLOW_DUMP_IN_LOGS) {
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				RecipientIdCache.canonicalTableDump();
				RecipientIdCache.dump();
				Conversation.dumpThreadsTable(context);
				Conversation.dump();
				Conversation.dumpSmsTable(context);
				Contact.dump();
			}
		}).start();
	}

	public static void warnPossibleRecipientMismatch(final String msg,
			final Activity activity) {
		Log.e(TAG, "WARNING!!!! " + msg, new RuntimeException());

		if (SHOW_SEVERE_WARNING_DIALOG) {
			dumpInternalTables(activity);
			activity.runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(activity)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle(R.string.error_state)
							.setMessage(
									msg
											+ "\n\n"
											+ activity
													.getString(R.string.error_state_text))
							.setPositiveButton(R.string.yes,
									new OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).show();
				}
			});
		}
	}

}
