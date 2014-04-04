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
	protected HashMap<Long, Item> itemsById;
	protected ArrayList<Item> items;
	protected HashMap<Long, Label> labelsById;
	protected HashMap<String, Label> labelsByName;
	protected ArrayList<Label> labels;
	protected HashMap<Long, Project> projectsById;
	protected HashMap<String, Project> projectsByName;
	protected ArrayList<Project> projects;
	
	protected TodoistData() {
		itemsById = new HashMap<Long, Item>();
		items = new ArrayList<Item>();
		labelsById = new HashMap<Long, Label>();
		labelsByName = new HashMap<String, Label>();
		labels = new ArrayList<Label>();
		projectsById = new HashMap<Long, Project>();
		projectsByName = new HashMap<String, Project>();
		projects = new ArrayList<Project>();
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
		if (!items.contains(item)) {
			item.addObserver(this);
			itemsById.put(item.getId(), item);
			items.add(item);
		
			this.setChanged();
			this.notifyObservers(items);
		}
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
		if (!labels.contains(label)) {
			label.addObserver(this);
			labelsById.put(label.getId(), label);
			labelsByName.put(label.getName(), label);
			labels.add(label);
			
			this.setChanged();
			this.notifyObservers(labels);
		}
	}
	
	public Label addLabel(String name, int color) {
		Label label = new Label(name, color);
		this.addLabel(label);
		return label;
	}
	
	/**
	 * Returns a label with the given id. Only used internal by the API
	 * @param id The id to look for
	 * @return The label with the given id
	 */
	protected Label getLabel(long id) {
		return labelsById.get(id);
	}
	
	/**
	 * Returns a label with the given name
	 * @param name The name of the label
	 * @return The label with the given name or null when it does not exists
	 */
	public Label getLabel(String name) {
		return labelsByName.get(name);
	}
	
	public ArrayList<Project> getProjects() {
		return projects;
	}
	
	/**
	 * Add a project to the Todoist account. Only used internal by the API
	 * @param item
	 */
	protected void addProject(Project project) {
		if (!projects.contains(project)) {
			project.addObserver(this);
			projectsById.put(project.getId(), project);
			projectsByName.put(project.getName(), project);
			projects.add(project);
			
			this.setChanged();
			this.notifyObservers(projects);
		}
	}
	
	/**
	 * Returns a project with the given id. Only used internal by the API
	 * @param id The id to look for
	 * @return The project with the given id
	 */
	protected Project getProject(long id) {
		return projectsById.get(id);
	}
	
	/**
	 * Returns a project with the given name
	 * @param name The name of the project
	 * @return A project, or null when it not exist
	 */
	public Project getProject(String name) {
		return projectsByName.get(name);
	}
	
	public String toString() {
		String retVal = "Projects:\n";
		for (Project project: projects) {
			retVal += project + "\n";
		}
		retVal += "\nItems:\n";
		for (Item item: items) {
			retVal += item + "\n";
		}
		retVal += "\nLabels:\n";
		for (Label label: labels) {
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
