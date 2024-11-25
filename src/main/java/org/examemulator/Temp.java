package org.examemulator;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.*;
import java.nio.charset.*; 

import java.net.*;

public class Temp {

    public static void main(String[] args) {
	System.setProperty("http.agent", "Chrome");

	final var text = "{\"data\":\"key=IAfpK, age=58, key=WNVdi, age=64, key=jp9zt, age=47\"}"
	.replace("{\"data\":", "").replace("\"}", ",");

//	final var splitText = text.split("[age=\\d+]");
	

var pattern = 
Pattern.compile("age=(.*?),").matcher(text)
       .results()                       // Stream<MatchResult>
       .map(mr -> mr.group(1).trim())
       .mapToInt(Integer::parseInt)
       //.filter(i -> i > 50)
       .count()
//       .forEach(System.out::println);
       ;
       

//	final var result = Arrays.stream(splitText)
//		.filter(t -> t.contains("age"))
////	          .map(text -> text.)
//		.collect(Collectors.toList());
//	;

	System.out.println(text);

	System.out.println(pattern);
    }
}
