package com.wemakestuff.mmsplus.location;

public interface ICountryDetector extends android.os.IInterface {
	/** Local-side IPC implementation stub class. */
	public static abstract class Stub extends android.os.Binder implements
			ICountryDetector {
		private static final java.lang.String DESCRIPTOR = "ICountryDetector";

		/** Construct the stub at attach it to the interface. */
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}

		/**
		 * Cast an IBinder object into an ICountryDetector interface, generating
		 * a proxy if needed.
		 */
		public static ICountryDetector asInterface(android.os.IBinder obj) {
			if ((obj == null)) {
				return null;
			}
			android.os.IInterface iin = (android.os.IInterface) obj
					.queryLocalInterface(DESCRIPTOR);
			if (((iin != null) && (iin instanceof ICountryDetector))) {
				return ((ICountryDetector) iin);
			}
			return new ICountryDetector.Stub.Proxy(obj);
		}

		public android.os.IBinder asBinder() {
			return this;
		}

		@Override
		public boolean onTransact(int code, android.os.Parcel data,
				android.os.Parcel reply, int flags)
				throws android.os.RemoteException {
			switch (code) {
			case INTERFACE_TRANSACTION: {
				reply.writeString(DESCRIPTOR);
				return true;
			}
			case TRANSACTION_detectCountry: {
				data.enforceInterface(DESCRIPTOR);
				Country _result = this.detectCountry();
				reply.writeNoException();
				if ((_result != null)) {
					reply.writeInt(1);
					_result.writeToParcel(reply,
							android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
				} else {
					reply.writeInt(0);
				}
				return true;
			}
			case TRANSACTION_addCountryListener: {
				data.enforceInterface(DESCRIPTOR);
				ICountryListener _arg0;
				_arg0 = ICountryListener.Stub.asInterface(data
						.readStrongBinder());
				this.addCountryListener(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_removeCountryListener: {
				data.enforceInterface(DESCRIPTOR);
				ICountryListener _arg0;
				_arg0 = ICountryListener.Stub.asInterface(data
						.readStrongBinder());
				this.removeCountryListener(_arg0);
				reply.writeNoException();
				return true;
			}
			}
			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements ICountryDetector {
			private android.os.IBinder mRemote;

			Proxy(android.os.IBinder remote) {
				mRemote = remote;
			}

			public android.os.IBinder asBinder() {
				return mRemote;
			}

			public java.lang.String getInterfaceDescriptor() {
				return DESCRIPTOR;
			}

			/**
			 * Start detecting the country that the user is in.
			 * 
			 * @return the country if it is available immediately, otherwise
			 *         null will be returned.
			 */
			public Country detectCountry() throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				Country _result;
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_detectCountry, _data,
							_reply, 0);
					_reply.readException();
					if ((0 != _reply.readInt())) {
						_result = Country.CREATOR.createFromParcel(_reply);
					} else {
						_result = null;
					}
				} finally {
					_reply.recycle();
					_data.recycle();
				}
				return _result;
			}

			/**
			 * Add a listener to receive the notification when the country is
			 * detected or changed.
			 */
			public void addCountryListener(ICountryListener listener)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeStrongBinder((((listener != null)) ? (listener
							.asBinder()) : (null)));
					mRemote.transact(Stub.TRANSACTION_addCountryListener,
							_data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			/**
			 * Remove the listener
			 */
			public void removeCountryListener(ICountryListener listener)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeStrongBinder((((listener != null)) ? (listener
							.asBinder()) : (null)));
					mRemote.transact(Stub.TRANSACTION_removeCountryListener,
							_data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}
		}

		static final int TRANSACTION_detectCountry = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
		static final int TRANSACTION_addCountryListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
		static final int TRANSACTION_removeCountryListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
	}

	/**
	 * Start detecting the country that the user is in.
	 * 
	 * @return the country if it is available immediately, otherwise null will
	 *         be returned.
	 */
	public Country detectCountry() throws android.os.RemoteException;

	/**
	 * Add a listener to receive the notification when the country is detected
	 * or changed.
	 */
	public void addCountryListener(ICountryListener listener)
			throws android.os.RemoteException;

	/**
	 * Remove the listener
	 */
	public void removeCountryListener(ICountryListener listener)
			throws android.os.RemoteException;
}