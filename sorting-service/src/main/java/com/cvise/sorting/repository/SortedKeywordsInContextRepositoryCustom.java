package com.cvise.sorting.repository;


public interface SortedKeywordsInContextRepositoryCustom {
	/**Append unseen keyword in context in context string into the 
	 * list of keywords of a certain id.
	 * 
	 * @param id String identifier of SortedKeywordsInContext to update keyword to.
	 * @param keywordInContext String keyword data to add to list.
	 */
	public void pushUniqueKeywordById(String id, String keywordInContext);
}