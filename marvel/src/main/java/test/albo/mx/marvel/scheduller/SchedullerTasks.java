package test.albo.mx.marvel.scheduller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import test.albo.mx.marvel.service.MarvelService;

@Slf4j
@Component
public class SchedullerTasks {
	
	@Autowired
	MarvelService marvelService;

	@Async
	@Scheduled(cron = "0 00 7 ? * MON-SUN")
	public void syncLibrary() {
		String uuid = UUID.randomUUID().toString();

		try {
			marvelService.syncLibrary(uuid);
			
		} catch (Exception e) {
			log.error("{} :: Error scheduler: {} ",uuid, e, getClass());
		}

	}
}
