package chatroom.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MsgObj implements Serializable{

	// For entering or exiting a room ##TOM: don't understand the need for these
	public static final byte exiting = 0;
	public static final byte entering = 1;
	public static final byte existingUser = 2;
	public static final byte newUser = 3;
	
	private byte msgType = 0;
	private ArrayList<Object> payload; // use an ArrayList and put the interpretation on the server. object because it allows us to do dirty things
	private List<String> targets = null;
	
	/**
	 * Creates a simple message for asking for Refreshes
	 * to the Chat Room listing
	 */
	public MsgObj () {
		this.payload = new ArrayList<Object>();
	}
	
	//## TOM: so many constructors :O how about we generalize it
	// Changes Made:
	// Use arraylist for all params
	// manually set the msgType for desired purpose in the class sending the message
	// avoids having logic in this class to set it
	// puts the interpretation of data on the receiving class, not this one
	// use one constructor. cleaner that way.
	
	/** 
	 * Method that either gives information to validate a user, or to create a new user
	 * @param username
	 * @param password
	 * @param userexists
	 */
		
	public void addToPayload(Object toAdd){
		this.payload.add(toAdd);
	}
	
	public void setType(byte type){
		this.msgType = type;
	}
	
	public byte getType() {
		return this.msgType;
	}
	
	public ArrayList<Object> getPayload(){
		return this.payload;
	}
}
