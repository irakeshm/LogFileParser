package parserUtility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BugReportParser {
	
	
	public static void main(String[] args){
		
		String[] serachText= {"WARNING","OOM","OutOfMemoryError"};
		String processId="4667";
		printLogFileDetails(processId,serachText[0]);
		
	}
	/**
	 * Description: This method calls different Print Methods based on the given requirement
	 * @param processId
	 * @param errorMessageString
	 */
	public static void printLogFileDetails(String processId, String errorMessageString){
			
		 try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(BugReportParser.class.getResourceAsStream("bugreport.txt")));
	                Stream<String> stream = reader.lines()) {
	            List<String> linesForGivenProcessId = new ArrayList<>();
	            linesForGivenProcessId = stream.filter(str -> Pattern.matches("\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}  "+processId+"  .*", str)).collect(Collectors.toList());
	            String[] linesArr = linesForGivenProcessId.stream().toArray(String[]::new);
	            
	            printFatalExceptionDetails(linesArr,linesForGivenProcessId.size());
	            printUniqueErrorMessages(linesForGivenProcessId);
	            printErrorMessageForGivenInput(linesForGivenProcessId,errorMessageString);
	            
		 } catch (Exception e) {
	            System.out.println(e);
	        }
	}
	
	/**
	 * Description: Find all FATAL crashes, and List “Unique Exception messages” 
	 * with the number of occurrences with List stack trace for every unique Fatal exception
	 * @param inputArray
	 * @param searchAreaSize
	 */
	public static void printFatalExceptionDetails(String[] inputArray, int searchAreaSize)
	{
		List<Integer> indexList = IntStream.range(0, searchAreaSize)
                .filter(i -> inputArray[i].contains("FATAL EXCEPTION")).mapToObj(i -> i).collect(Collectors.toList());

        Map<String, LogDetail> map = new HashMap<>();
        for (int i : indexList) {
            String currLine = inputArray[i];
            int lastColonIndex = currLine.indexOf(":", 18);
            String key = inputArray[i + 2].substring(lastColonIndex + 1);
            if (!map.containsKey(key)) {
                LogDetail ld = new LogDetail();
                ld.setCount(1);
                if (!key.contains("no stack trace available")) {
                    ld.setStacktrace(findStackTrace(i + 3, lastColonIndex, inputArray,
                            currLine.substring(18, lastColonIndex)));
                }
                map.put(key, ld);
            } else {
                LogDetail ld = map.get(key);
                ld.count++;
            }
        }
        System.out.println("FATAL EXCEPTION");
        System.out.println(" ============== ");
        System.out.println("Exception Message| # of Occurrences");
        for (Entry<String, LogDetail> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "|" + entry.getValue().getCount());
        }
        
        
        System.out.println("Stacktrace:");
        System.out.println(" ============== ");
        for (Entry<String, LogDetail> entry : map.entrySet()) {
            System.out.println(entry.getKey());            
            for(String s: entry.getValue().getStacktrace())
            	System.out.println(s);
        }
	}
	/**
	 * Description: Find all errors with log level E and List Unique errors with the number of occurrences
	 * @param inputList
	 */
	public static void printUniqueErrorMessages(List<String> inputList)
	{
		List<String> errorLogLines = inputList.stream().filter(str -> 
        {
            return Pattern.matches("\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}  4667  \\d* E .*", str);
        }).collect(Collectors.toList());
		
		Map<String, Integer> errorMap = new HashMap<>();
		
		int occuranceCount=0;
		for(String error:errorLogLines)
		{
			int lastColonIndex = error.indexOf(":", 18);
			String key = error.substring(lastColonIndex + 1);
			if(!key.contains("FATAL EXCEPTION") && !key.contains("        at") && !key.contains("PID: 4667"))
			{
				if (!errorMap.containsKey(key)) {
					occuranceCount=1;
					errorMap.put(key, occuranceCount);
				}
				else {
					occuranceCount=errorMap.get(key);
					occuranceCount++;
					errorMap.put(key, occuranceCount);
				}
			}
		}
		
		System.out.println("Errors");
        System.out.println("=====");
        System.out.println("Error Message| # of Occurrences");
		for (Entry<String, Integer> entry : errorMap.entrySet()) {
            System.out.println(entry.getKey() + "|" + entry.getValue());
        }

	}
	/**
	 * Description: For the given Strings List unique lines containing that string and the number of occurrences
	 * @param inputList
	 * @param searchText
	 */
	public static void printErrorMessageForGivenInput(List<String> inputList, String searchText)
	{
		List<String> errorLines=inputList.stream().filter(str ->str.contains(searchText)).collect(Collectors.toList());
		
		Map<String, Integer> errorMap = new HashMap<>();
		
		int occuranceCount=0;
		for(String error:errorLines)
		{
			int lastColonIndex = error.indexOf(":", 18);
			String key = error.substring(lastColonIndex + 1);
			
			if (!errorMap.containsKey(key)) {
				occuranceCount=1;
				errorMap.put(key, occuranceCount);
			}
			else {
				occuranceCount=errorMap.get(key);
				occuranceCount++;
				errorMap.put(key, occuranceCount);
			}
			
		}
		
		System.out.println("Matching Strings");
        System.out.println("=============");
        System.out.println("Matching String| # of Occurrences");
		for (Entry<String, Integer> entry : errorMap.entrySet()) {
            System.out.println(entry.getKey() + "|" + entry.getValue());
        }
		
		
	}
	/**
	 * Log Details Class to map with Fatal Exceptions, and get the occurance and Stack Traces.
	 * @author RKMishra
	 *
	 */
	public static class LogDetail {
	        Integer count;
	        List<String> stacktrace=new ArrayList<>();

	        public Integer getCount() {
	            return count;
	        }

	        public void setCount(Integer count) {
	            this.count = count;
	        }

	        public List<String> getStacktrace() {
	            return stacktrace;
	        }

	        public void setStacktrace(List<String> stacktrace) {
	            this.stacktrace = stacktrace;
	        }
	    }
	/**
	 * Description: Finds the Stack Traces in Exception List
	 * @param lineNum
	 * @param lastColonIndex
	 * @param lines
	 * @param searchStr
	 * @return
	 */
	private static List<String> findStackTrace(int lineNum, int lastColonIndex, String[] lines, String searchStr) {
        List<String> stackTrace = new ArrayList<>();
        while (lineNum < lines.length && lines[lineNum].indexOf(searchStr) == 18
                && lines[lineNum].indexOf("        at", 18 + searchStr.length()) == lastColonIndex + 1) {
            stackTrace.add(lines[lineNum].substring(lastColonIndex + 1));
            lineNum++;
        }
        return stackTrace;
    }

}
