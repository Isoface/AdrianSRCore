package com.hotmail.AdrianSR.core.util;

public class Matcher {
	
	private final CharSequence sequence;
	
	public Matcher(CharSequence sequence) {
		if (sequence == null) {
			throw new IllegalArgumentException("The sequence cannot be null!");
		}
		this.sequence = sequence;
	}
	
	public Matcher(char[] sequence) {
		if (sequence == null) {
			throw new IllegalArgumentException("The sequence cannot be null!");
		}
		this.sequence = new String(sequence);
	}
	
	public Matcher(byte[] sequence) {
		if (sequence == null) {
			throw new IllegalArgumentException("The sequence cannot be null!");
		}
		this.sequence = new String(sequence);
	}
	
	public int matches(CharSequence other) {
		if (other == null) {
			throw new IllegalArgumentException("The sequence cannot be null!");
		}
		
		int  count = 0;
		for (int x = 0; x < sequence.length(); x++) {
			for (int y = 0; y < other.length(); y++) {
				if (sequence.charAt(x) == other.charAt(y)) {
					count ++;
				}
			}
		}
		return count;
	}
	
	public int matchesIgnoreCase(CharSequence other) {
		if (other == null) {
			throw new IllegalArgumentException("The sequence cannot be null!");
		}
		
		int  count = 0;
		for (int x = 0; x < sequence.length(); x++) {
			for (int y = 0; y < other.length(); y++) {
				if (Character.toLowerCase(sequence.charAt(x)) == Character.toLowerCase(other.charAt(y))) {
					count ++;
				}
			}
		}
		return count;
	}
	
	public boolean hasMatches(CharSequence other) {
		return (matches(other) > 0);
	}
	
	public boolean hasMatchesIgnoreCase(CharSequence other) {
		return (matchesIgnoreCase(other) > 0);
	}
	
	public String replaceFirst(CharSequence other, CharSequence replacement) {
		if (other == null || other.length() < 1) {
			throw new IllegalArgumentException("The sequence cannot be null or empty!");
		}
		
		for (int x = 0; x < other.length(); x++) {
			for (int y = 0; y < sequence.length(); y++) {
				if (other.charAt(x) == sequence.charAt(y)) {
					return other.toString().replaceFirst(new String(new char[] { other.charAt(x) }), replacement.toString());
				}
			}
		}
		return other.toString();
	}
	
	public String replaceFirstIgnoreCase(CharSequence other, CharSequence replacement) {
		if (other == null || other.length() < 1) {
			throw new IllegalArgumentException("The sequence cannot be null or empty!");
		}
		
		for (int x = 0; x < other.length(); x++) {
			for (int y = 0; y < sequence.length(); y++) {
				if (Character.toLowerCase(other.charAt(x)) == Character.toLowerCase(sequence.charAt(y))) {
					return other.toString().replaceFirst(new String(new char[] { other.charAt(x) }), replacement.toString());
				}
			}
		}
		return other.toString();
	}
	
	public String replace(CharSequence other, CharSequence replacement) {
		if (other == null || other.length() < 1) {
			throw new IllegalArgumentException("The sequence cannot be null or empty!");
		}
		
		String clean = other.toString();
		for (int x = 0; x < other.length(); x++) {
			for (int y = 0; y < sequence.length(); y++) {
				if (other.charAt(x) == sequence.charAt(y)) {
					clean = clean.replace(new String(new char[] { other.charAt(x) }), replacement.toString());
				}
			}
		}
		return clean;
	}
	
	public String replaceIgnoreCase(CharSequence other, CharSequence replacement) {
		if (other == null || other.length() < 1) {
			throw new IllegalArgumentException("The sequence cannot be null or empty!");
		}
		
		String clean = other.toString();
		for (int x = 0; x < other.length(); x++) {
			for (int y = 0; y < sequence.length(); y++) {
				if (Character.toLowerCase(other.charAt(x)) == Character.toLowerCase(sequence.charAt(y))) {
					clean = clean.replace(new String(new char[] { other.charAt(x) }), replacement.toString());
				}
			}
		}
		return clean;
	}
}