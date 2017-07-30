package test.java.util.date;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for DateUtils.
 * 
 * @author Rohan Dhapodkar
 *
 */
public class DateUtilsTest {
	
	private Date startDate;

	@BeforeClass
	public static void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("EST"));
	}
	
	@Before
	public void init() throws ParseException {
		
		startDate = DateUtils.parseDate("1970.01.01 00:00:00", "yyyy.MM.dd HH:mm:ss");
	}
	
	@Test
	public void test_timezone(){
		Assertions.assertThat(startDate.toString()).isEqualTo("Thu Jan 01 00:00:00 EST 1970");
		
	}
	
	@Test
	public void test_commons_truncate_date() throws ParseException {
		Date date = DateUtils.parseDate("1970.01.03 23:59:59", "yyyy.MM.dd HH:mm:ss");
		Date truncatedDate = test.java.util.date.DateUtils.truncateDate(date);
		Assertions.assertThat(truncatedDate)
		.hasMinute(0)
		.hasHourOfDay(0)
		.hasMillisecond(0)
		.hasDayOfMonth(3)
		.hasMonth(1)
		.hasYear(1970)
		.hasSecond(0);
	}
	
	@Test
	public void test_truncate_on_2017_03_12_DST_Start() throws ParseException {
		Date date = DateUtils.parseDate("2017.03.12 00:00:00", "yyyy.MM.dd HH:mm:ss");
		this.test_truncate_for_entire_day(date);
		
	}
	
	@Test
	public void test_truncate_on_2017_11_05_DST_End() throws ParseException {
		Date date = DateUtils.parseDate("2017.11.05 00:00:00", "yyyy.MM.dd HH:mm:ss");
		this.test_truncate_for_entire_day(date);
		
	}
	
	public void test_truncate_for_entire_day(Date date) {
		Date expectedDate = (Date) date.clone();
		Date nextDate = DateUtils.addDays(date, 1);
		test.java.util.date.DateUtils.clearCache();
		while(date.before(nextDate)) {
			Assertions.assertThat(test.java.util.date.DateUtils.getMapSize()).isEqualTo(0);
			Date truncatedDate = test.java.util.date.DateUtils.truncateDate(date);
			Assertions.assertThat(truncatedDate)
			.hasMinute(0)
			.hasHourOfDay(0)
			.hasMillisecond(0)
			.isEqualTo(expectedDate);
			
			Assertions.assertThat(test.java.util.date.DateUtils.getMapSize()).isEqualTo(1);
			test.java.util.date.DateUtils.clearCache();			
			
			date = DateUtils.addSeconds(date, 1);
		}
	}
	
	@Test
	public void test_next_date_from_1970_till_today_today() {
		Date date = startDate;
		Date currentDate = new Date();
		while(date.before(currentDate)) {
			Date actualNextDate = test.java.util.date.DateUtils.getNextDate(date);
			Date expectedNextDate = DateUtils.addDays(date, 1);
			Assertions.assertThat(actualNextDate).isEqualTo(expectedNextDate);
			date = expectedNextDate;
		}
		int size = test.java.util.date.DateUtils.getMapSize();
		System.out.println("Size is "+size);
	}
	
	@Test
	public void test_truncate_date_from_1970_till_today_today() {
		Date date = DateUtils.addHours(startDate,2);
		Date currentDate = new Date();
		while(date.before(currentDate)) {
			Date actualTruncatedDate = test.java.util.date.DateUtils.truncateDate(date);
			Assertions.assertThat(actualTruncatedDate)
			.hasMinute(0)
			.hasHourOfDay(0)
			.hasMillisecond(0)
			.hasSecond(0)
			.isEqualToIgnoringHours(date);
			
			Assertions.assertThat(date)
			.hasHourOfDay(2);
			date = DateUtils.addDays(date, 1);
		}
		int size = test.java.util.date.DateUtils.getMapSize();
		System.out.println("Size is "+size);
	}
}
