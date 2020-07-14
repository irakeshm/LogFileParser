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
		
		printLogFileDetails("4667","");
		
	}
	
	public static void printLogFileDetails(String processId, String errorMessageString){
			
		 try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(BugReportParser.class.getResourceAsStream("bugreport.txt")));
	                Stream<String> stream = reader.lines()) {
	            List<String> linesForGivenProcessId = new ArrayList<>();
	            linesForGivenProcessId = stream.filter(str -> Pattern.matches("\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}  "+processId+"  .*", str)).collect(Collectors.toList());
	            String[] linesArr = linesForGivenProcessId.stream().toArray(String[]::new);
	            
	            //printFatalExceptionDetails(linesArr,linesForGivenProcessId.size());
	            printUniqueErrorMessages(linesForGivenProcessId);
	            
		 } catch (Exception e) {
	            System.out.println(e);
	        }
	}
	
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
	
	public static void printUniqueErrorMessages(List<String> lines)
	{
		List<String> errorLogLines = lines.stream().filter(str -> 
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
