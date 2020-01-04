package net.mysticcloud.spigot.core.runnables;

import java.util.Calendar;

import org.bukkit.Bukkit;

import net.mysticcloud.spigot.core.Main;
import net.mysticcloud.spigot.core.utils.CoreUtils;
import net.mysticcloud.spigot.core.utils.Holiday;

public class DateChecker implements Runnable {

	@Override
	public void run() {
		
		

		CoreUtils.setHoliday(Holiday.NONE);

//		if (CoreUtils.getMonth() == Calendar.JANUARY) {
//			if (CoreUtils.getDay() == 1)
//				CoreUtils.setHoliday(Holiday.NEW_YEARS);
//			if (CoreUtils.getDay() == 16) {
//				CoreUtils.setHoliday(Holiday.NEW_YEARS);
//			}
//		}
//		
//		if(CoreUtils.getMonth() == Calendar.FEBRUARY){
//			CoreUtils.setHoliday(Holiday.VALENTINES);
//		}
//
//		if (CoreUtils.getMonth() == Calendar.OCTOBER) {
//			if (CoreUtils.getDay() == 31)
//				CoreUtils.setHoliday(Holiday.HALLOWEEN);
//
//		}
//		if (CoreUtils.getMonth() == Calendar.NOVEMBER) {
//		}
		if (CoreUtils.getMonth() == Calendar.DECEMBER) {
			if (CoreUtils.getDay() <= 26)
				CoreUtils.setHoliday(Holiday.CHRISTMAS);

//			if (CoreUtils.getDay() >= 27) 
//				CoreUtils.setHoliday(Holiday.NEW_YEARS);
//
//			
		}

//		if(CoreUtils.getHoliday() != Holiday.NONE){
//			CoreUtils.runRunnables();
//		}
		
		if(CoreUtils.getHoliday() != Holiday.NONE)Bukkit.getScheduler().runTask(Main.getPlugin(), new HolidayParticles());
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new DateChecker(), 1);
	}

}
