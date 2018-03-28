package de.jayaware.calendarserver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import de.jayaware.calendarserver.model.CalendarDateListField;
import lombok.Data;

public class TestArschtritt {

	static int userSize = 101;
	static Lorem lorem = LoremIpsum.getInstance();
	Gson gson = new Gson();
	static List<String> users = new ArrayList<>();

	@Data
	class Wrapper {
		List<CalendarDateListField> entries;
	}

	Wrapper loadedWrapper;

	@BeforeClass
	public static void fillUsers() {
		for (int i = 0; i <= userSize; i++) {
			users.add(lorem.getName());
		}
	}

	@Test
	public void canWeTrustThisShit() throws IOException {

		Wrapper wrapper = new Wrapper();
		List<CalendarDateListField> rows = new ArrayList<>();

		long entrySize = userSize * 80 * 12;
		long currentTimeMillis = System.currentTimeMillis();

		long distance = 100000;

		for (int i = 0; i < entrySize; i++) {
			int maxGuests = new Random().nextInt((10 - 1) + 1);
			int userIndex = new Random().nextInt((users.size() - 1) + 1);
			List<String> guests = new ArrayList<>();
			for (int j = 0; j < maxGuests; j++) {
				int invited = new Random().nextInt((users.size() - 1) + 1);
				guests.add(users.get(invited));
			}
			currentTimeMillis += distance += distance;
			rows.add(CalendarDateListField.builder().start(currentTimeMillis).end(currentTimeMillis)
					.body(lorem.getParagraphs(1, 10)).userId(users.get(userIndex)).invited(guests)
					.subject(lorem.getWords(1, 10)).build());
		}

		wrapper.setEntries(rows);
		Files.write(Paths.get("bigFatDump.json"), gson.toJson(wrapper).getBytes(StandardCharsets.UTF_8));

	}

	@Test
	public void howToRead() throws JsonSyntaxException, IOException {
		loadedWrapper = gson.fromJson(
				new String(Files.readAllBytes(Paths.get("bigFatDump.json")), StandardCharsets.UTF_8), Wrapper.class);

		System.err.println("Loaded " + new Date());
		System.err.println(users.size());
		int userIndex = new Random().nextInt(users.size());

		String user = users.get(userIndex);
		System.err.println("Suche für " + user);
		List<CalendarDateListField> allForUser = loadedWrapper.getEntries().stream()
				.filter(entry -> entry.getUserId().equals(user)).collect(Collectors.toList());

		System.err.println(user + " hatte " + allForUser.size() + " termine ");
		System.err.println("Queried " + new Date());

		CalendarDateListField anyDate = allForUser.get(new Random().nextInt(allForUser.size()));
		CalendarDateListField anySecond = allForUser.get(new Random().nextInt(allForUser.size()));

		long myStart = anyDate.getStart() < anySecond.getStart() ? anyDate.getStart() : anySecond.getStart();
		long myEnd = anyDate.getEnd() < anySecond.getEnd() ? anyDate.getEnd() : anySecond.getEnd();

		List<CalendarDateListField> matched = allForUser.stream()
				.filter(entry -> entry.getStart() > myStart && entry.getEnd() < myEnd).collect(Collectors.toList());

		System.err.println(matched.size() + " between " + new Date());

		List<CalendarDateListField> allMatched = loadedWrapper.getEntries().stream()
				.filter(entry -> entry.getStart() > myStart && entry.getEnd() < myEnd).collect(Collectors.toList());

		System.err.println(allMatched.size() + " between " + new Date());

	}

}
