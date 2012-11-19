package com.wemakestuff.mmsplus.util;

public class PhoneNumberUtils extends android.telephony.PhoneNumberUtils {
	// Three and four digit phone numbers for either special services,
	// or 3-6 digit addresses from the network (eg carrier-originated SMS
	// messages) should
	// not match.
	//
	// This constant used to be 5, but SMS short codes has increased in length
	// and
	// can be easily 6 digits now days. Most countries have SMS short code
	// length between
	// 3 to 6 digits. The exceptions are
	//
	// Australia: Short codes are six or eight digits in length, starting with
	// the prefix "19"
	// followed by an additional four or six digits and two.
	// Czech Republic: Codes are seven digits in length for MO and five (not
	// billed) or
	// eight (billed) for MT direction
	//
	// see http://en.wikipedia.org/wiki/Short_code#Regional_differences for
	// reference
	//
	// However, in order to loose match 650-555-1212 and 555-1212, we need to
	// set the min match
	// to 7.
	static final int MIN_MATCH = 7;

	/**
	 * Replace arabic/unicode digits with decimal digits.
	 * 
	 * @param number
	 *            the number to be normalized.
	 * @return the replaced number.
	 * 
	 * @hide
	 */
	public static String replaceUnicodeDigits(String number) {
		StringBuilder normalizedDigits = new StringBuilder(number.length());
		for (char c : number.toCharArray()) {
			int digit = Character.digit(c, 10);
			if (digit != -1) {
				normalizedDigits.append(digit);
			} else {
				normalizedDigits.append(c);
			}
		}
		return normalizedDigits.toString();
	}

	/**
	 * Compare phone numbers a and b, return true if they're identical enough
	 * for caller ID purposes.
	 * 
	 * - Compares from right to left - requires MIN_MATCH (7) characters to
	 * match - handles common trunk prefixes and international prefixes
	 * (basically, everything except the Russian trunk prefix)
	 * 
	 * Note that this method does not return false even when the two phone
	 * numbers are not exactly same; rather; we can call this method
	 * "similar()", not "equals()".
	 * 
	 * @hide
	 */
	public static boolean compareLoosely(String a, String b) {
		int ia, ib;
		int matched;
		int numNonDialableCharsInA = 0;
		int numNonDialableCharsInB = 0;

		if (a == null || b == null)
			return a == b;

		if (a.length() == 0 || b.length() == 0) {
			return false;
		}

		ia = indexOfLastNetworkChar(a);
		ib = indexOfLastNetworkChar(b);
		matched = 0;

		while (ia >= 0 && ib >= 0) {
			char ca, cb;
			boolean skipCmp = false;

			ca = a.charAt(ia);

			if (!isDialable(ca)) {
				ia--;
				skipCmp = true;
				numNonDialableCharsInA++;
			}

			cb = b.charAt(ib);

			if (!isDialable(cb)) {
				ib--;
				skipCmp = true;
				numNonDialableCharsInB++;
			}

			if (!skipCmp) {
				if (cb != ca && ca != WILD && cb != WILD) {
					break;
				}
				ia--;
				ib--;
				matched++;
			}
		}

		if (matched < MIN_MATCH) {
			int effectiveALen = a.length() - numNonDialableCharsInA;
			int effectiveBLen = b.length() - numNonDialableCharsInB;

			// if the number of dialable chars in a and b match, but the matched
			// chars < MIN_MATCH,
			// treat them as equal (i.e. 404-04 and 40404)
			if (effectiveALen == effectiveBLen && effectiveALen == matched) {
				return true;
			}

			return false;
		}

		// At least one string has matched completely;
		if (matched >= MIN_MATCH && (ia < 0 || ib < 0)) {
			return true;
		}

		/*
		 * Now, what remains must be one of the following for a match:
		 * 
		 * - a '+' on one and a '00' or a '011' on the other - a '0' on one and
		 * a (+,00)<country code> on the other (for this, a '0' and a '00'
		 * prefix would have succeeded above)
		 */

		if (matchIntlPrefix(a, ia + 1) && matchIntlPrefix(b, ib + 1)) {
			return true;
		}

		if (matchTrunkPrefix(a, ia + 1) && matchIntlPrefixAndCC(b, ib + 1)) {
			return true;
		}

		if (matchTrunkPrefix(b, ib + 1) && matchIntlPrefixAndCC(a, ia + 1)) {
			return true;
		}

		return false;
	}

	/**
	 * Phone numbers are stored in "lookup" form in the database as reversed
	 * strings to allow for caller ID lookup
	 * 
	 * This method takes a phone number and makes a valid SQL "LIKE" string that
	 * will match the lookup form
	 * 
	 */
	/**
	 * all of a up to len must be an international prefix or
	 * separators/non-dialing digits
	 */
	private static boolean matchIntlPrefix(String a, int len) {
		/* '([^0-9*#+pwn]\+[^0-9*#+pwn] | [^0-9*#+pwn]0(0|11)[^0-9*#+pwn] )$' */
		/* 0 1 2 3 45 */

		int state = 0;
		for (int i = 0; i < len; i++) {
			char c = a.charAt(i);

			switch (state) {
			case 0:
				if (c == '+')
					state = 1;
				else if (c == '0')
					state = 2;
				else if (isNonSeparator(c))
					return false;
				break;

			case 2:
				if (c == '0')
					state = 3;
				else if (c == '1')
					state = 4;
				else if (isNonSeparator(c))
					return false;
				break;

			case 4:
				if (c == '1')
					state = 5;
				else if (isNonSeparator(c))
					return false;
				break;

			default:
				if (isNonSeparator(c))
					return false;
				break;

			}
		}

		return state == 1 || state == 3 || state == 5;
	}

	/**
	 * all of 'a' up to len must be a (+|00|011)country code) We're fast and
	 * loose with the country code. Any \d{1,3} matches
	 */
	private static boolean matchIntlPrefixAndCC(String a, int len) {
		/* [^0-9*#+pwn]*(\+|0(0|11)\d\d?\d? [^0-9*#+pwn] $ */
		/* 0 1 2 3 45 6 7 8 */

		int state = 0;
		for (int i = 0; i < len; i++) {
			char c = a.charAt(i);

			switch (state) {
			case 0:
				if (c == '+')
					state = 1;
				else if (c == '0')
					state = 2;
				else if (isNonSeparator(c))
					return false;
				break;

			case 2:
				if (c == '0')
					state = 3;
				else if (c == '1')
					state = 4;
				else if (isNonSeparator(c))
					return false;
				break;

			case 4:
				if (c == '1')
					state = 5;
				else if (isNonSeparator(c))
					return false;
				break;

			case 1:
			case 3:
			case 5:
				if (isISODigit(c))
					state = 6;
				else if (isNonSeparator(c))
					return false;
				break;

			case 6:
			case 7:
				if (isISODigit(c))
					state++;
				else if (isNonSeparator(c))
					return false;
				break;

			default:
				if (isNonSeparator(c))
					return false;
			}
		}

		return state == 6 || state == 7 || state == 8;
	}

	/** all of 'a' up to len must match non-US trunk prefix ('0') */
	private static boolean matchTrunkPrefix(String a, int len) {
		boolean found;

		found = false;

		for (int i = 0; i < len; i++) {
			char c = a.charAt(i);

			if (c == '0' && !found) {
				found = true;
			} else if (isNonSeparator(c)) {
				return false;
			}
		}

		return found;
	}

	/**
	 * index of the last character of the network portion (eg anything after is
	 * a post-dial string)
	 */
	static private int indexOfLastNetworkChar(String a) {
		int pIndex, wIndex;
		int origLength;
		int trimIndex;

		origLength = a.length();

		pIndex = a.indexOf(PAUSE);
		wIndex = a.indexOf(WAIT);

		trimIndex = minPositive(pIndex, wIndex);

		if (trimIndex < 0) {
			return origLength - 1;
		} else {
			return trimIndex - 1;
		}
	}

	/** or -1 if both are negative */
	static private int minPositive(int a, int b) {
		if (a >= 0 && b >= 0) {
			return (a < b) ? a : b;
		} else if (a >= 0) { /* && b < 0 */
			return a;
		} else if (b >= 0) { /* && a < 0 */
			return b;
		} else { /* a < 0 && b < 0 */
			return -1;
		}
	}

}
