package com.cvise.circular_shift.respository;


public interface KeywordsInContextRepositoryCustom {
	/**Append unseen keyword in context in context string into the 
	 * list of keywords of a certain id.
	 * 
	 * @param id String identifier of KeywordsInContext to update keyword to.
	 * @param keywordInContext String keyword data to add to list.
	 */
	public void pushUniqueKeywordById(String id, String keywordInContext);
}