package hello;

import java.util.List;

/**
 * @author woemler
 */
public interface BlogOperations {
	List<String> findTagsByFragment(String fragment);
	List<String> findAllTags();
}
