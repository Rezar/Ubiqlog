package com.ubiqlog.vis.utils;

/**
 * User friendly Exception - used to display a message to user
 * 
 * @author Victor Gugonatu
 * @date 10.2010
 * @version 1.0
 */
public class UserFriendlyException extends Exception {

	private static final long serialVersionUID = 1L;

	public UserFriendlyException(String message) {
		super(message);
	}

}
