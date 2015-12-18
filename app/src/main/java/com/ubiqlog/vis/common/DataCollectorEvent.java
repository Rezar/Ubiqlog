package com.ubiqlog.vis.common;

import java.util.EventObject;

/**
 * 
 * @author Dorin Gugonatu
 * 
 */

public abstract class DataCollectorEvent extends EventObject 
{
	private static final long serialVersionUID = -8583986592518797549L;

	public DataCollectorEvent(Object source) 
	{
		super(source);
	}

}
