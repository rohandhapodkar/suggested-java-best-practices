package test.java.util.date;

import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * <p>
 * If your application is doing frequent date manipulations like truncate,
 * addDate, get Next/Previous business date, then it's worth caching those
 * results.
 * <p>
 * Below is the DateUtils implementation using {@link ConcurrentSkipListMap} for
 * efficient date look up. This DateUtils class pre computes results for Date
 * operations like truncate, nextDate.
 * <p>
 * <p>
 * This is just a prototype which can be extended further as per your
 * requirements. eg.
 * <li>Caching first/last day of the month, adding some pre-configured days
 * like SLA timelines from given date.</li>
 * <li>Eager initialization of dates map.</li>
 * <li>Return List/Iterator for all dates between given dates and so on.</li>
 * <p>
 * This prototype assumes 3 MB memory is not a constrain for your system and
 * you are looking for performance efficient solution for frequent date calculations.
 * <br>
 * VisualVM shows retain size as 3 MB for all dates from 1970-1-1 to 2017-07-26.
 * 
 * <p><p>
 * <u>
 * Note:- This DateUtils is tested for all dates from 1970-01-01 till 2017-07-26. It is
 * also tested for the day's when DST is turned ON and OFF. Refer to test cases for more details {@link DateUtilsTest}
 * </u>
 * @author Rohan Dhapodkar
 *
 */

public class DateUtils {
	private static ConcurrentSkipListMap<Long, DateCalculations> dateMap = new ConcurrentSkipListMap<>();
	
	/**
	 * Class to hold Date calculations
	 * @author Rohan Dhapodkar
	 *
	 */
	static class DateCalculations {
		
		Date keyDate;
		Date nextDate;
		
		public DateCalculations(Date inputDate) {
			keyDate = org.apache.commons.lang3.time.DateUtils.truncate(inputDate, Calendar.DATE);			
			nextDate = org.apache.commons.lang3.time.DateUtils.addDays(keyDate, 1);			
		}
		
		protected boolean coversDate(Date date) {
			long longDate = date.getTime();
			return this.keyDate.getTime() <= longDate && longDate < this.nextDate.getTime();
		}

		@Override
		public String toString() {
			return "DateCalculations [keyDate=" + keyDate + ", nextDate=" + nextDate + "]";
		}		
	}
	
	protected static int getMapSize() {
		return dateMap.size();
	}
	
	private static DateCalculations getDateCalculations(Date date) {
		Entry<Long, DateCalculations> existingEntry = dateMap.floorEntry(date.getTime());
		if(existingEntry != null && existingEntry.getValue().coversDate(date)) {
			return existingEntry.getValue();
		}
		DateCalculations newDateCalculation = new DateCalculations(date);
		
		assert newDateCalculation.coversDate(date) :" Failed ";
		
		dateMap.putIfAbsent(newDateCalculation.keyDate.getTime(), newDateCalculation);
		
		return getDateCalculations(date);
	}
	
	public static Date truncateDate(Date date) {
		return getWrappedDate(getDateCalculations(date).keyDate);
	}
	
	public static Date getNextDate(Date date) {
		return getWrappedDate(getDateCalculations(date).nextDate);
	}
	
	protected static Date getWrappedDate(Date date) {
		return (Date) date.clone();
	}
	
	public static void clearCache() {
		dateMap.clear();
	}
}
