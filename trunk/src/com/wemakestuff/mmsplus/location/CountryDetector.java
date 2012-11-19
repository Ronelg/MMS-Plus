package com.wemakestuff.mmsplus.location;

import java.util.HashMap;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class provides access to the system country detector service. This
 * service allows applications to obtain the country that the user is in.
 * <p>
 * The country will be detected in order of reliability, like
 * <ul>
 * <li>Mobile network</li>
 * <li>Location</li>
 * <li>SIM's country</li>
 * <li>Phone's locale</li>
 * </ul>
 * <p>
 * Call the {@link #detectCountry()} to get the available country immediately.
 * <p>
 * To be notified of the future country change, use the
 * {@link #addCountryListener}
 * <p>
 * <p>
 * You do not instantiate this class directly; instead, retrieve it through
 * {@link android.content.Context#getSystemService
 * Context.getSystemService(Context.COUNTRY_DETECTOR)}.
 * <p>
 * Both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions are needed.
 *
 * @hide
 */
public class CountryDetector {

    /**
     * The class to wrap the ICountryListener.Stub and CountryListener objects
     * together. The CountryListener will be notified through the specific
     * looper once the country changed and detected.
     */
    private final static class ListenerTransport extends ICountryListener.Stub {

        private final CountryListener mListener;

        private final Handler mHandler;

        public ListenerTransport(CountryListener listener, Looper looper) {
            mListener = listener;
            if (looper != null) {
                mHandler = new Handler(looper);
            } else {
                mHandler = new Handler();
            }
        }

        public void onCountryDetected(final Country country) {
            mHandler.post(new Runnable() {
                public void run() {
                    mListener.onCountryDetected(country);
                }
            });
        }
    }

    private final static String TAG = "CountryDetector";
    private final ICountryDetector mService;
    private final HashMap<CountryListener, ListenerTransport> mListeners;

    /**
     * @hide - hide this constructor because it has a parameter of type
     *       ICountryDetector, which is a system private class. The right way to
     *       create an instance of this class is using the factory
     *       Context.getSystemService.
     */
    public CountryDetector(ICountryDetector service) {
        mService = service;
        mListeners = new HashMap<CountryListener, ListenerTransport>();
    }

    /**
     * Start detecting the country that the user is in.
     *
     * @return the country if it is available immediately, otherwise null will
     *         be returned.
     */
    public Country detectCountry() {
        try {
            return mService.detectCountry();
        } catch (RemoteException e) {
            Log.e(TAG, "detectCountry: RemoteException", e);
            return null;
        }
    }

    /**
     * Add a listener to receive the notification when the country is detected
     * or changed.
     *
     * @param listener will be called when the country is detected or changed.
     * @param looper a Looper object whose message queue will be used to
     *        implement the callback mechanism. If looper is null then the
     *        callbacks will be called on the main thread.
     */
    public void addCountryListener(CountryListener listener, Looper looper) {
        synchronized (mListeners) {
            if (!mListeners.containsKey(listener)) {
                ListenerTransport transport = new ListenerTransport(listener, looper);
                try {
                    mService.addCountryListener(transport);
                    mListeners.put(listener, transport);
                } catch (RemoteException e) {
                    Log.e(TAG, "addCountryListener: RemoteException", e);
                }
            }
        }
    }

    /**
     * Remove the listener
     */
    public void removeCountryListener(CountryListener listener) {
        synchronized (mListeners) {
            ListenerTransport transport = mListeners.get(listener);
            if (transport != null) {
                try {
                    mListeners.remove(listener);
                    mService.removeCountryListener(transport);
                } catch (RemoteException e) {
                    Log.e(TAG, "removeCountryListener: RemoteException", e);
                }
            }
        }
    }
}