package it.tooly.fxtooly.tab.social.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.tooly.fxtooly.ToolyUtils;

public class Message {
	private String from;
	private String text;
	private Date when;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getWhen() {
		return when;
	}
	public void setWhen(Date when) {
		this.when = when;
	}
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		Message om = (Message) o;
		return om.getWhen().getTime() == getWhen().getTime();
	}
	@JsonIgnore
	public String getFormattedDate(){
		return ToolyUtils.format(when);
	}
}
