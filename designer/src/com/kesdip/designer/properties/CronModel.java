package com.kesdip.designer.properties;

public class CronModel {
	
	private CronDatum cronMinute = new CronDatum(); // 0 - 59
	private CronDatum cronHour = new CronDatum(); // 0 - 23
	private CronDatum cronDayOfMonth = new CronDatum(); // 1 - 31
	private CronDatum cronMonth = new CronDatum(); // 1 - 12
	private CronDatum cronDayOfWeek = new CronDatum(); // 0 - 6 (Sunday = 0 or 6)
	private CronDatum cronYear = new CronDatum();
	
	public CronDatum getCronHour() {
		return cronHour;
	}

	public CronDatum getCronMinute() {
		return cronMinute;
	}

	public CronDatum getCronDayOfMonth() {
		return cronDayOfMonth;
	}

	public CronDatum getCronMonth() {
		return cronMonth;
	}

	public CronDatum getCronDayOfWeek() {
		return cronDayOfWeek;
	}

	public CronDatum getCronYear() {
		return cronYear;
	}

	public void setCron(String cron) {
		String[] cronTokens = cron.split(" ");
		cronMinute.setCron(cronTokens[0]);
		cronHour.setCron(cronTokens[1]);
		cronDayOfMonth.setCron(cronTokens[2]);
		cronMonth.setCron(cronTokens[3]);
		cronDayOfWeek.setCron(cronTokens[4]);
		
		if (cronTokens.length == 6)
			cronYear.setCron(cronTokens[5]);
		else
			cronYear.reset();
		
	}
	
	public String getCron() {
		StringBuilder sb = new StringBuilder();
		sb.append(cronMinute.getCron()).append(' ');
		sb.append(cronHour.getCron()).append(' ');
		sb.append(cronDayOfMonth.getCron()).append(' ');
		sb.append(cronMonth.getCron()).append(' ');
		sb.append(cronDayOfWeek.getCron());
		return sb.toString();
	}

}
