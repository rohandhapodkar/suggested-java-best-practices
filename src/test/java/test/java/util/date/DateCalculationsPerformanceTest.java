package test.java.util.date;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case to simulate 10M calls to org.apache.commons.lang3.time.DateUtils in
 * multiple parallel threads.
 * 
 * @author Rohan Dhapodkar
 *
 */
public class DateCalculationsPerformanceTest {
	static int nThreads ;
	static ExecutorService service;
	long iterations = 10000000L;
	Date testDate = new Date();


	@BeforeClass
	public static void initOnce() throws ParseException {
		nThreads = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(nThreads);
		
		Date date = DateUtils.parseDate("1970.01.01 00:00:00", "yyyy.MM.dd HH:mm:ss");
		Date currentDate = new Date();
		while(date.before(currentDate)) {
			date = test.java.util.date.DateUtils.getNextDate(date);
		}
		int size = test.java.util.date.DateUtils.getMapSize();
		System.out.println("Size is "+size);
	}
	
	@Before
	public void initDate() throws ParseException {
		testDate = DateUtils.parseDate("2000.01.01 00:00:00", "yyyy.MM.dd HH:mm:ss");
	}

	@AfterClass
	public static void shutDown() {
		service.shutdownNow();
	}

	public void runJob(long iterations, Runnable runnableJob, ExecutorService service) throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(nThreads);

		Runnable job = () -> {
			try {
				runnableJob.run();
			} finally {
				latch.countDown();
			}
		};

		for (int index = 0; index < nThreads; index++) {
			service.execute(job);
		}

		Thread.sleep(0, 10);
		Map<Thread, StackTraceElement[]> stackTrace = Thread.getAllStackTraces();
		stackTrace.forEach((thread, trace) -> {
			System.out.println("\nThread " + thread.getName());
			Stream.of(trace).forEach(t -> {
				System.out.println(t);
			});

		});
		latch.await();
	}

	@Test
	public void test_loop_time() throws InterruptedException {
		AtomicLong l = new AtomicLong();
		this.runJob(iterations, () -> {
			long localLong = 0;
			for (long counter = 0; counter < iterations; counter++) {
				localLong++;
			}
			l.addAndGet(localLong);
		}, this.service);
	}
	
	@Test
	public void test_using_apache_commons_truncate() throws InterruptedException {
		this.runJob(iterations, () -> {
			for (long counter = 0; counter < iterations; counter++) {
				DateUtils.truncate(testDate, Calendar.HOUR_OF_DAY);
			}
		}, this.service);
	}
	
	@Test
	public void test_using_new_DateUtils() throws InterruptedException {
		this.runJob(iterations, () -> {
			for (long counter = 0; counter < iterations; counter++) {
				test.java.util.date.DateUtils.truncateDate(testDate);
			}
		}, this.service);
	}			
}
