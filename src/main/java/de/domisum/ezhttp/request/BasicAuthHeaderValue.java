package de.domisum.ezhttp.request;

import de.domisum.lib.auxilium.util.java.annotations.API;

import javax.annotation.Nonnull;
import java.util.Base64;

@API
public class BasicAuthHeaderValue implements CharSequence
{

	private final String asString;


	// INIT
	@API
	public BasicAuthHeaderValue(String username, String password)
	{
		String valueUnencoded = username+":"+password;
		asString = "Basic "+Base64.getEncoder().encodeToString(valueUnencoded.getBytes());
	}


	// STRING
	@Override
	public int length()
	{
		return asString.length();
	}

	@Override
	public char charAt(int index)
	{
		return asString.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end)
	{
		return asString.subSequence(start, end);
	}

	@Nonnull
	@Override
	public String toString()
	{
		return asString;
	}

}