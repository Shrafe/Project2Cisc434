package chatroom.client;

import java.util.List;

public class MsgObj {

	// For entering or exiting a room
	public static final byte exiting = 0;
	public static final byte entering = 1;
	public static final byte existingUser = 2;
	public static final byte newUser = 3;
	
	private byte msgType = 0;
	private String user = null;
	private String password = null;
	private String message = null;
	private String room = null;
	private String target = null;
	private List<String> targets = null;
	
	/**
	 * Creates a simple message for asking for Refreshes
	 * to the Chat Room listing
	 */
	public MsgObj () {
		this.msgType = 0;
	}
	
	/**
	 * Creates a message for Entering or Leaving a Chat Room
	 * 
	 * @param room - The room the user is joining/leaving
	 * @param user - The user who is joining/leaving
	 * @param direction - Number indicating if they're exiting or leaving
	 */
	public MsgObj (String room, String user, byte direction) {
		
		this.room = room;
		this.user = user;
		
		if (direction == exiting) {
			msgType = 1;
		}
		else {
			msgType = 2;
		}
	}
	
	/**
	 * Creates a message for posting to a Chat Room
	 * 
	 * @param msg - The message to post
	 * @param room - The Chat Room to post to
	 * @param user - The user who wishes to post to the room
	 */
	public MsgObj (String msg, String room, String user) {
		
		this.message = msg;
		this.room = room;
		this.user = user;
		msgType = 3;
	}
	
	/**
	 * Creates a message for whispering a target user
	 * 
	 * @param msg - The message to post
	 * @param room - The Chat Room to post to
	 * @param user - The user who wishes to post to the room
	 * @param target -  The target user who the sending user wishes to send a message to
	 */
	public MsgObj (String msg, String room, String user, String target) {
		
		this.message = msg;
		this.room = room;
		this.user = user;
		this.target = target;
		msgType = 4;
	}
	
	/**
	 * Creates a message for whispering a selection of targeted users
	 * 
	 * @param msg - The message to post
	 * @param room - The Chat Room to post to
	 * @param user - The user who wishes to post to the room
	 * @param targets - The list of target users who the sending user wishes to send a message to
	 */
	public MsgObj (String msg, String room, String user, List<String> targets) {
		
		this.message = msg;
		this.room = room;
		this.user = user;
		this.targets = targets;
		msgType = 5;
	}
	
	public MsgObj (byte userexists, String username, String password) {
		
		this.user = username;
		this.password = password;
		
		if (userexists == existingUser) {
			msgType = 6;
		} else {
			msgType = 7;
		}
	}
	
	public byte getType() {
		return msgType;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getTarget() {
		return target;
	}
	
	public List<String> getAllTargets() {
		return targets;
	}
	
	public String getRoom() {
		return room;
	}
}
