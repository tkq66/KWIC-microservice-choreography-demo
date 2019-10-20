package com.cvise.input.repository;


public interface InputRepositoryCustom {
	/**Append unseen input string into the corpus of Input of a certain id.
	 * 
	 * @param id String identifier of Input to update corpus to.
	 * @param input String data to add to corpus.
	 */
	public void pushUniqueCorpusById(String id, String input);
}