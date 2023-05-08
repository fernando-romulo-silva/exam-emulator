package org.examemulator.util;

import java.util.List;

public class FileUtil {

    public static final List<String> WORDS_ALL = List.of( //
		    "All of these", //
		    "All answers are true", //
		    "All answers are valid", //
		    "All of these are", //
		    "All of the options", //
		    "All of the preceding options", //
		    "All of the above", //
		    "All of the scenarios are valid", //
		    "All of the preceding", //
		    "All of the preceding options are true", //
		    "All of the preceding sentences are true", // 
		    "All of the preceding steps are required", //
		    "All of the preceding answers are correct",
		    "All of the preceding sentences are wrong",
		    "All of the options" //
    );

    public static final List<String> WORDS_NONE = List.of(//
		    "None of these", //
		    "None of the options", //
		    "None of the options are valid", //
		    "None of the above", //
		    "None of the answers", //
		    "None of the preceding answers are true",
		    "None of the preceding answers are correct", //
		    "None of the preceding sentences are right"
    );

//    public static final List<String> WORDS_ABOVE = List.of("Both the above" , "Both of the above");

}
