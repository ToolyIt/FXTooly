package it.tooly.fxtooly.tab.social.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javafx.beans.property.StringProperty;

public class Channel {
	private StringProperty name;
	private List<Message> messages = new LinkedList<Message>();
	@JsonIgnore
	private int vstamp = -99;
	public StringProperty getName() {
		return name;
	}
	public void setName(StringProperty name) {
		this.name = name;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	public int getVstamp() {
		return vstamp;
	}
	public void setVstamp(int vstamp) {
		this.vstamp = vstamp;
	}
}
