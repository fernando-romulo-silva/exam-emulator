package org.examemulator.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomUtil {
    
    private RandomUtil() {
	throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param <T>
     * @param inputList
     * @param percentage percentage of elements that should be randomly extracted from inputFiles
     * @return
     */

    public static <T> List<T> getRandomSubList(final List<T> inputList, final double percentage) {
	if ((percentage < 0) || (percentage > 1)) {
	    throw new IllegalArgumentException("percentage has to be between 0 and 1");
	}

	int numberOfElements = (int) (inputList.size() * percentage);
	return getRandomSubList(inputList, numberOfElements);
    }

    /**
     *
     * @param <T>
     * @param inputList
     * @param numberOfFiles number of files that should be randomly extracted from inputFiles
     * @return
     */
    public static <T> List<T> getRandomSubList(final List<T> inputList, final int numberOfFiles) {

	if ((numberOfFiles < 0) || (numberOfFiles > inputList.size())) {
	    throw new IllegalArgumentException("numberOfFiles has to be between 0 and size of inputList");
	}

	final var shuffeledList = new ArrayList<T>(inputList);
	Collections.shuffle(shuffeledList);

	return shuffeledList.subList(0, numberOfFiles);
    }

}
