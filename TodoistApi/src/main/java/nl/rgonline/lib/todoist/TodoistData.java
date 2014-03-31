package nl.rgonline.lib.todoist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * This class represents a full Todoist Object. It contains items, etc, etc, etc
 * It is observable, so that it notifies the user when something has changed. There are 
 * two ways the data can be changed:
 *   - The user updates an internal object of this TodoistData object
 *   - The API updates an internal object of this TodoistData object after the user called syncAndGet
 *   
 * The object that changed will always be supplied when it is a list it means that something in that list 
 * is updated (added or removed), when it is an object it means that something in that object changed.
 * 
 * @author ruudgreven
 *
 */
public class TodoistData extends Observable implements Observer {
	private HashMap<Integer, Item> itemsById;
	private ArrayList<Item> items;
	private HashMap<Integer, Label> labelsById;
	private ArrayList<Label> labels;
	
	protected TodoistData() {
		itemsById = new HashMap<Integer, Item>();
		items = new ArrayList<Item>();
		labelsById = new HashMap<Integer, Label>();
		labels = new ArrayList<Label>();
	}
	
	/**
	 * Returns all the items of the Todoist account
	 * @return
	 */
	public ArrayList<Item> getItems() {
		return items;
	}
	
	/**
	 * Add an item to to the Todoist account. Only used internal by the API
	 * @param item
	 */
	protected void addItem(Item item) {
		item.addObserver(this);
		itemsById.put(item.getId(), item);
		items.add(item);
		
		this.setChanged();
		this.notifyObservers(itemsById);
	}
	
	/**
	public void addItem(String content, Project project, int indent, int priority, String date_string, String due_date_utc, int assigned_by_uid, int responsible_uid, int item_order) {
		
	}
	**/

	public ArrayList<Label> getLabels() {
		return labels;
	}
	
	/**
	 * Add a label to to the Todoist account. Only used internal by the API
	 * @param item
	 */
	protected void addLabel(Label label) {
		label.addObserver(this);
		labelsById.put(label.getId(), label);
		labels.add(label);
		
		this.setChanged();
		this.notifyObservers(labelsById);
	}
	
	/**
	 * Returns a label with the given id. Only used internal by the API
	 * @param id The id to look for
	 * @return The label with the given id
	 */
	protected Label getLabel(int id) {
		return labelsById.get(id);
	}
	
	public String toString() {
		String retVal = "Items:\n";
		for (Item item: itemsById.values()) {
			retVal += item + "\n";
		}
		retVal += "\nLabels:\n";
		for (Label label: labelsById.values()) {
			retVal += label + "\n";
		}
		
		return retVal;
	}
	
	/**
	 * Update method, called when something changed inside the model. It notifies all the observers of this model
	 */
	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}
}
