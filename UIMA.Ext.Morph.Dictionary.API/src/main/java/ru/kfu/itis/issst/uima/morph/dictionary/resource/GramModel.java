/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary.resource;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import ru.kfu.itis.issst.uima.morph.model.Grammeme;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface GramModel {

	int getGrammemMaxNumId();

	int getGrammemNumId(String gramId);

	Grammeme getGrammem(int numId);

	/**
	 * @param id
	 * @return grammeme with given string id or null if it does not exist.
	 */
	Grammeme getGrammem(String id);

	/**
	 * @param gramId
	 * @param includeTarget
	 *            if true given gramId will be included in result set
	 * @return bitset containing numerical ids of given grammeme children
	 *         (including grandchildren and so on). If grammeme with given
	 *         string id does not exist then result is null.
	 */
	BitSet getGrammemWithChildrenBits(String gramId, boolean includeTarget);

	/**
	 * 
	 * @return grammems whose parent id is null.
	 */
	Set<String> getTopGrammems();

	/**
	 * 
	 * @param grammems
	 *            grammem bits
	 * @return list of string ids ordered by grammeme numerical id (ascending)
	 */
	List<String> toGramSet(BitSet grammems);

	BitSet getPosBits();

	/**
	 * @return PoS-label from the given gram bits if there are any; otherwise -
	 *         null.
	 * @throws IllegalArgumentException
	 *             if there are > 1 PoS-bits set in the given bitset.
	 */
	String getPos(BitSet gramBits);
}
