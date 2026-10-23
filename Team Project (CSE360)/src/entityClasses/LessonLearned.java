package entityClasses;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a lesson learned and all associated epic attributes.
 * Provides accessors, validation, and a display representation.
 *
 * @author HW2 Team
 * @version 2.00
 */
public class LessonLearned {

    private int id;
    private String title;
    private String lessonText;
    private String author;

    // Additional lesson content fields from the team epics.
    private String rootCause;
    private String recommendation;
    private String keywords;
    private String timeEffort;
    private String category;

    // Curator controls.
    private boolean isLocked;
    private Set<String> lockedFields;
    private boolean effortVisible;

    /**
     * Creates a lesson learned without an assigned database id.
     *
     * @param title the lesson title
     * @param lessonText the lesson description or lesson text
     * @param author the username of the person creating the lesson
     */
    public LessonLearned(String title, String lessonText, String author) {
        this(0, title, lessonText, author);
    }

    /**
     * Creates a lesson learned with an existing database id.
     *
     * @param id the database id
     * @param title the lesson title
     * @param lessonText the lesson description or lesson text
     * @param author the username of the author
     */
    public LessonLearned(int id, String title, String lessonText, String author) {
        this.id = id;
        this.title = clean(title);
        this.lessonText = clean(lessonText);
        this.author = clean(author);
        this.rootCause = "";
        this.recommendation = "";
        this.keywords = "";
        this.timeEffort = "";
        this.category = "";
        this.isLocked = false;
        this.lockedFields = new HashSet<>();
        this.effortVisible = true;
    }

    /**
     * Converts null text to an empty string and trims surrounding whitespace.
     *
     * @param value the value to clean
     * @return a non-null trimmed string
     */
    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    /** @return the database id, or zero before insertion */
    public int getId() { return id; }

    /** @param id the database id */
    public void setId(int id) { this.id = id; }

    /** @return the lesson title */
    public String getTitle() { return title; }

    /** @param title the lesson title */
    public void setTitle(String title) { this.title = clean(title); }

    /** @return the lesson text/description */
    public String getLessonText() { return lessonText; }

    /** @param lessonText the lesson text/description */
    public void setLessonText(String lessonText) { this.lessonText = clean(lessonText); }

    /** @return the lesson author username */
    public String getAuthor() { return author; }

    /** @param author the lesson author username */
    public void setAuthor(String author) { this.author = clean(author); }

    /** @return the lesson root cause */
    public String getRootCause() { return rootCause; }

    /** @param rootCause the lesson root cause */
    public void setRootCause(String rootCause) { this.rootCause = clean(rootCause); }

    /** @return the recommendation for future contributors */
    public String getRecommendation() { return recommendation; }

    /** @param recommendation the recommendation text */
    public void setRecommendation(String recommendation) {
        this.recommendation = clean(recommendation);
    }

    /** @return lesson keywords */
    public String getKeywords() { return keywords; }

    /** @param keywords lesson keywords, stored as text */
    public void setKeywords(String keywords) { this.keywords = clean(keywords); }

    /** @return time and effort information */
    public String getTimeEffort() { return timeEffort; }

    /** @param timeEffort time and effort information */
    public void setTimeEffort(String timeEffort) { this.timeEffort = clean(timeEffort); }

    /** @return lesson category */
    public String getCategory() { return category; }

    /** @param category lesson category */
    public void setCategory(String category) { this.category = clean(category); }

    /** @return true if the entire lesson is locked */
    public boolean getIsLocked() { return isLocked; }

    /** @return true if the entire lesson is locked */
    public boolean isLocked() { return isLocked; }

    /** @param isLocked whether the entire lesson is locked */
    public void setIsLocked(boolean isLocked) { this.isLocked = isLocked; }

    /** @param isLocked whether the entire lesson is locked */
    public void setLocked(boolean isLocked) { this.isLocked = isLocked; }

    /** @return an unmodifiable copy of the locked field names */
    public Set<String> getLockedFields() {
        return Collections.unmodifiableSet(new HashSet<>(lockedFields));
    }

    /** @param fields the names of fields that contributors cannot edit */
    public void setLockedFields(Set<String> fields) {
        this.lockedFields = fields == null ? new HashSet<>() : new HashSet<>(fields);
    }

    /**
     * Marks a field as locked or unlocked.
     *
     * @param fieldName the field name
     * @param locked true to lock the field; false to unlock it
     */
    public void setFieldLocked(String fieldName, boolean locked) {
        String field = clean(fieldName);
        if (field.isEmpty()) return;
        if (locked) lockedFields.add(field);
        else lockedFields.remove(field);
    }

    /**
     * Checks whether a field is locked.
     *
     * @param fieldName the field name
     * @return true if the field is locked
     */
    public boolean isFieldLocked(String fieldName) {
        return fieldName != null && lockedFields.contains(fieldName.trim());
    }

    /** @return true if time/effort information is visible to contributors */
    public boolean getEffortVisible() { return effortVisible; }

    /** @return true if time/effort information is visible to contributors */
    public boolean isEffortVisible() { return effortVisible; }

    /** @param effortVisible whether time/effort information is visible */
    public void setEffortVisible(boolean effortVisible) {
        this.effortVisible = effortVisible;
    }

    /**
     * Validates the lesson before database storage.
     *
     * @return an empty string when valid, otherwise the first validation message
     */
    public String validate() {
        if (title.isEmpty()) return "The lesson title cannot be empty.";
        if (title.length() > 100) return "The lesson title cannot be longer than 100 characters.";
        if (lessonText.isEmpty()) return "The lesson description cannot be empty.";
        if (lessonText.length() > 2000) return "The lesson description cannot be longer than 2000 characters.";
        if (author.isEmpty()) return "The lesson author cannot be empty.";
        if (rootCause.length() > 2000) return "Root cause cannot be longer than 2000 characters.";
        if (recommendation.length() > 2000) return "Recommendation cannot be longer than 2000 characters.";
        if (keywords.length() > 1000) return "Keywords cannot be longer than 1000 characters.";
        if (timeEffort.length() > 255) return "Time/effort information cannot be longer than 255 characters.";
        if (category.length() > 100) return "Category cannot be longer than 100 characters.";
        return "";
    }

    /**
     * Returns the lesson in a form suitable for display in a list.
     *
     * @return a readable representation of the lesson
     */
    @Override
    public String toString() {
        return "[" + id + "] " + title + " - " + author;
    }
}
