package nl.rgonline.lib.todoist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

/**
 * This class represents a full Todoist Object. It contains items, projects and labels (at this moment that's all)
 * It is observable, so that it notifies the user when something has changed. There are 
 * two ways the data can be changed:
 *   - The user updates an internal object of this TodoistData object
 *   - The API updates an internal object of this TodoistData object after the user called syncAndGet
 *   
 * The object that changed will always be supplied, there are 2 types of updates:
 *   - Addition, removal of items, projects or labels. In this case you receive the ArrayList with items, projects or labels in the update methode as second argument
 *   - Update of the internal data of an item, project or label. In this case you receive the Item, Project or Label that has been updated
 * 
 * @author ruudgreven
 *
 */
public class TodoistData extends Observable implements Observer {
	protected HashMap<Long, Item> itemsById;
	protected ArrayList<Item> items;
	protected HashMap<Long, Label> labelsById;
	protected ArrayList<Label> labels;
	protected HashMap<Long, Project> projectsById;
	protected ArrayList<Project> projects;
	
	protected TodoistData() {
		itemsById = new HashMap<Long, Item>();
		items = new ArrayList<Item>();
		labelsById = new HashMap<Long, Label>();
		labels = new ArrayList<Label>();
		projectsById = new HashMap<Long, Project>();
		projects = new ArrayList<Project>();
	}
	
	/**
	 * Returns all the items of the Todoist account
	 * @return
	 */
	public ArrayList<Item> getItems() {
		return items;
	}
	
	protected Item getItemByContent(String content) {
		for (Item item: items) {
			if (item.getContent().equals(content)) {
				return item;
			}
		}
		return null;
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
	 * Add a item with the given content to the inbox
	 * @param content The content (text) for the item
	 */
	public void addItem(String content) {
		Project inboxProject = getProjectByName("Inbox");
		this.addItem(content, inboxProject, 1, 1, null, null, 0, 0, 1);
	}
	
	/**
	 * Add a item with the given characteristics
	 * @param content The content (text) for the item
	 * @param project The project to which the item belongs
	 */
	public void addItem(String content, Project project) {
		this.addItem(content, project, 1, 1, null, null, 0, 0, 1);
	}
	
	/**
	 * Add a item with the given characteristics
	 * @param content The content (text) for the item
	 * @param project The project to which the item belongs
	 * @param indent The indent
	 */
	public void addItem(String content, Project project, int indent) {
		this.addItem(content, project, indent, 1, null, null, 0, 0, 1);
	}
	
	/**
	 * Add a item with the given characteristics
	 * @param content The content (text) for the item
	 * @param project The project to which the item belongs
	 * @param indent The indent
	 * @param priority The priority of the item
	 */
	public void addItem(String content, Project project, int indent, int priority) {
		this.addItem(content, project, indent, priority, null, null, 0, 0, 1);
	}
	
	/**
	 * Add a item with the given characteristics
	 * @param content The content (text) for the item
	 * @param project The project to which the item belongs
	 * @param indent The indent
	 * @param priority The priority of the item
	 * @param date_string The date string for the due date
	 * @param due_date The due date
	 */
	public void addItem(String content, Project project, int indent, int priority, String date_string, Date due_date) {
		this.addItem(content, project, indent, priority, date_string, due_date, 0, 0, 1);
	}
	
	/**
	 * Add a item with the given characteristics
	 * @param content The content (text) for the item
	 * @param project The project to which the item belongs
	 * @param indent The indent
	 * @param priority The priority of the item
	 * @param date_string The date string for the due date
	 * @param due_date The due date
	 * @param item_order The item_order of this item
	 */
	public void addItem(String content, Project project, int indent, int priority, String date_string, Date due_date, int item_order) {
		this.addItem(content, project, indent, priority, date_string, due_date, 0, 0, item_order);
	}
	
	/**
	 * Add a item with the given characteristics
	 * @param content The content (text) for the item
	 * @param project The project to which the item belongs
	 * @param indent The indent
	 * @param priority The priority of the item
	 * @param date_string The date string for the due date
	 * @param due_date The due date
	 * @param assigned_by_user The uid of the user to which this task is assigned to
	 * @param responsible_uid The uid of the user who is responsible for this task
	 * @param item_order The item_order of this item
	 */
	public void addItem(String content, Project project, int indent, int priority, String date_string, Date due_date, int assigned_by_user, int responsible_uid, int item_order) {
		if (project==null) {
			return;
		}
		Item item = new Item(this, content, project, indent, priority, date_string, due_date, assigned_by_user, responsible_uid, item_order);
		if (!items.contains(item)) {
			items.add(item);
			
			this.setChanged();
			this.notifyObservers();
		}
	}
	
	/**
	 * Returns the item with the given content
	 * @param content The content to search for
	 * @return The item when found, else null
	 */
	public Item getItem(String content) {
		return this.getItemByContent(content);
	}
	
	/**
	 * Get the item with the given content in the given project
	 * @param content The content to search for
	 * @param project The project to look for the item
	 * @return The project when found, else null
	 */
	public Item getItem(String content, Project project) {
		for (Item i: items) {
			if (i.getContent()!=null && i.getProject()!=null) {
				if (i.getContent().equals(content) && i.getProject().equals(project)) {
					return i;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns all the labels of the Todoist account
	 * @return The labels
	 */
	public ArrayList<Label> getLabels() {
		return labels;
	}
	
	protected Label getLabelByName(String name) {
		for (Label label: labels) {
			if (label.getName().equals(name)) {
				return label;
			}
		}
		return null;
	}
	
	/**
	 * Add a label to to the Todoist account. Only used internal by the API
	 * @param item
	 */
	protected void addLabel(Label label) {
		if (!labels.contains(label)) {
			label.addObserver(this);
			labelsById.put(label.getId(), label);
			labels.add(label);
			
			this.setChanged();
			this.notifyObservers(labels);
		}
	}
	
	/**
	 * Add a label with the given name and color
	 * @param name The name for the label
	 * @param color The color for the label
	 */
	public void addLabel(String name) {
		this.addLabel(name, 1);
	}
	
	/**
	 * Add a label with the given name and color
	 * @param name The name for the label
	 * @param color The color for the label
	 */
	public void addLabel(String name, int color) {
		Label label = new Label(this, name, color);
		if (!labels.contains(label)) {
			labels.add(label);
			
			this.setChanged();
			this.notifyObservers(labels);
		}
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
		return getLabelByName(name);
	}
	
	public ArrayList<Project> getProjects() {
		return projects;
	}
	
	protected Project getProjectByName(String name) {
		for (Project project: projects) {
			if (project.getName().equals(name)) {
				return project;
			}
		}
		return null;
	}
	
	/**
	 * Add a project to the Todoist account. Only used internal by the API
	 * @param item
	 */
	protected void addProject(Project project) {
		if (!projects.contains(project)) {
			project.addObserver(this);
			projectsById.put(project.getId(), project);
			projects.add(project);
			
			this.setChanged();
			this.notifyObservers(projects);
		}
	}
	
	/**
	 * Adds a project
	 * @param name The name of the project
	 */
	public void addProject(String name) {
		this.addProject(name, 1, 0, 1);
	}
	
	/**
	 * Adds a project
	 * @param name The name of the project
	 * @param color The color of the project
	 */
	public void addProject(String name, int color) {
		this.addProject(name, color, 0, 1);
	}
	
	/**
	 * Adds a project
	 * @param name The name of the project
	 * @param color The color of the project
	 * @param indent The indent for the project
	 * @param item_order The item_order for the project
	 */
	public void addProject(String name, int color, int indent, int item_order) {
		Project project = new Project(this, name, color, indent, item_order);
		if (!projects.contains(project)) {
			projects.add(project);
			
			this.setChanged();
			this.notifyObservers();
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
		return getProjectByName(name);
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
