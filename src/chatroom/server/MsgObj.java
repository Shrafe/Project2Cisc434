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
	 * Creates a message for Entering or Leaving a Chat Room
	 * 
	 * @param room - The room the user is joining/leaving
	 * @param user - The user who is joining/leaving (the user is already associated with the server thread,
	 * once validated. we don't need to indicate each message object who is doing what. that's already known
	 * @param direction - Number indicating if they're exiting or leaving
	 */
	public MsgObj (String room, byte direction) {
		this.payload.add(room);
		if (direction == exiting) {
			this.msgType = 1;
		}
		else {
			this.msgType = 2;
		}
	}
	
	/**
	 * Creates a message for posting to a Chat Room
	 * 
	 * @param msg - The message to post
	 * @param room - The Chat Room to post to
	 */
	public MsgObj (String msg, String room) {
		this.payload.add(room);
		this.payload.add(msg);
		this.msgType = 3;
	}
	
	/**
	 * Creates a message for whispering a selection of targeted users
	 * ##TOM: We can use this for single whispers too, targets just is of length 1
	 * 
	 * @param msg - The message to post
	 * @param room - The Chat Room to post to
	 * @param targets - The list of target users who the sending user wishes to send a message to
	 */
	public MsgObj (String msg, String room,  List<String> targets) {
		this.payload.add(room);
		this.payload.add(msg);
		this.targets = targets;
		this.msgType = 4;
	}
	
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
	
	public List<String> getAllTargets() {
		return this.targets;
	}
}
