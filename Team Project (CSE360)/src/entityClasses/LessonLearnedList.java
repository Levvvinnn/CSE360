package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: LessonLearnedList Class. </p>
 *
 * <p> Description: Stores the current lessons learned and supports creation
 * of subsets for searches.  A subset is a separate list, so an empty result,
 * a one-element result, or an arbitrarily large result can all be represented.</p>
 *
 * @author HW2 Team
 * @version 1.00
 */
public class LessonLearnedList {

    private final List<LessonLearned> lessons;

    /**
     * Creates an empty lesson list.
     */
    public LessonLearnedList() {
        lessons = new ArrayList<LessonLearned>();
    }

    /**
     * Creates a lesson list from an existing collection.
     *
     * @param source the lessons to copy into this list
     */
    public LessonLearnedList(List<LessonLearned> source) {
        lessons = new ArrayList<LessonLearned>();
        if (source != null) {
            lessons.addAll(source);
        }
    }

    /**
     * Adds a lesson to the list.
     *
     * @param lesson the lesson to add
     */
    public void addLesson(LessonLearned lesson) {
        if (lesson != null) {
            lessons.add(lesson);
        }
    }

    /**
     * Removes a lesson by database id.
     *
     * @param id the database id of the lesson
     * @return true if a lesson was removed; false otherwise
     */
    public boolean removeById(int id) {
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getId() == id) {
                lessons.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a subset using a case-insensitive search over the lesson title,
     * lesson text, and author.
     *
     * @param searchText the search text; null or blank returns all lessons
     * @return a new list containing only matching lessons
     */
    public LessonLearnedList createSubset(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new LessonLearnedList(lessons);
        }

        String search = searchText.trim().toLowerCase();
        LessonLearnedList subset = new LessonLearnedList();

        for (LessonLearned lesson : lessons) {
            if (lesson.getTitle().toLowerCase().contains(search)
                    || lesson.getLessonText().toLowerCase().contains(search)
                    || lesson.getAuthor().toLowerCase().contains(search)) {
                subset.addLesson(lesson);
            }
        }

        return subset;
    }

    /**
     * Returns a copy of the current lessons.
     *
     * @return a list containing the lessons currently stored here
     */
    public List<LessonLearned> getLessons() {
        return new ArrayList<LessonLearned>(lessons);
    }

    /**
     * Returns the number of lessons in this list.
     *
     * @return the number of lessons
     */
    public int size() {
        return lessons.size();
    }

    /**
     * Finds a lesson in this list by id.
     *
     * @param id the database id
     * @return the matching lesson, or null if it is not present
     */
    public LessonLearned findById(int id) {
        for (LessonLearned lesson : lessons) {
            if (lesson.getId() == id) {
                return lesson;
            }
        }
        return null;
    }
}