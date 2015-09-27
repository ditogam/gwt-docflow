package com.docflow.client.components;

import java.util.HashMap;

import com.docflow.client.components.docflow.PBankLive;
import com.docflow.client.components.docflow.PZoneChange;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * A Canvas that manages its children similar to Swing CardLayout. It treats
 * each component in the container as a card. Only one card is visible at a
 * time, and the container acts as a stack of cards. The first component added
 * to a CardLayout object is the visible component when the container is first
 * displayed.
 * 
 * @author farrukh@wellfleetsoftware.com
 */
public class CardLayoutCanvas extends VLayout {

	HashMap<Object, Canvas> cards = null;
	private Canvas currentCard = null;

	public static final String DOCFLOW_PANEL = "DOCFLOW_PANEL";
	public static final String LIVE_PANEL = "LIVE_PANEL";
	public static final String READER_LIST_PANEL = "READER_LIST_PANEL";
	public static final String JOB_POSSITION_PANEL = "PERSON_PANEL";
	public static final String DEPARTMENT_PANEL = "DEPARTMENT_PANEL";
	public static final String ZONECHANGE_PANEL = "ZONECHANGE_PANEL";
	public static final String COEF_PANEL = "COEF_PANEL";
	public static final String PLOMB_PANEL = "PLOMB_PANEL";
	public static final String CUSTOM_PANEL = "CUSTOM_PANEL";

	public CardLayoutCanvas() {
		cards = new HashMap<Object, Canvas>();
	}

	public void addCard(Object key, Canvas card) {

		Canvas[] oldMembers = this.getMembers();
		this.removeMembers(oldMembers);
		card.setWidth100();
		card.setHeight100();
		card.setPageLeft(0);
		card.setPageTop(0);
		this.addMember(card);
		cards.put(key, card);
		currentCard = card;
		if (card instanceof PZoneChange)
			((PZoneChange) card).clearFields();
		if (card instanceof PBankLive)
			((PBankLive) card).clearFields();
	}

	/**
	 * @return the currentCard
	 */
	public Canvas getCurrentCard() {
		return currentCard;
	}

	public void removeCard(Object key) {
		cards.remove(key);
	}

	public void showCard(Object key) {
		for (Object _key : cards.keySet()) {
			Canvas c = cards.get(_key);
			if (key.equals(_key)) {
				addCard(_key, c);
				break;
			}
		}
	}

	/*
	 * //Issue: show() draws all cards when it should only draw the shown card
	 * 
	 * @Override public void show() {
	 * 
	 * }
	 */
}