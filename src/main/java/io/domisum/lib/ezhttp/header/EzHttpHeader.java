package io.domisum.lib.ezhttp.header;

import io.domisum.lib.auxiliumlib.annotations.API;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

@API
@EqualsAndHashCode
public class EzHttpHeader
{
	
	@Getter
	private final String key;
	@Getter
	private final String value;
	
	
	// INIT
	@API
	public EzHttpHeader(CharSequence key, CharSequence value)
	{
		this(key.toString(), value.toString());
	}
	
	@API
	public EzHttpHeader(String key, String value)
	{
		validateKey(key);
		validateValue(value);
		
		this.key = key;
		this.value = value;
	}
	
	private void validateKey(String key)
	{
		Validate.notNull(key);
		Validate.isTrue(!key.contains(":"), "header key can't contain colon (:)");
	}
	
	private void validateValue(String value)
	{
		Validate.notNull(value);
	}
	
	
	// OBJECT
	@Override
	public String toString()
	{
		return key+": "+value;
	}
	
}
