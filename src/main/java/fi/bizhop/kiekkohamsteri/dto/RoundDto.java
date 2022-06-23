package fi.bizhop.kiekkohamsteri.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@AllArgsConstructor
@Getter
@Setter
public class RoundDto implements Comparable<RoundDto> {
	private String tournament;
	private String link;
	private String date;
	private int round;
	private int score;
	private int rating;
	private int holes;
	private boolean included;
	private boolean doubled;

    @Override
    public int compareTo(RoundDto o) {
        try {
            DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            Date from = sdf.parse(this.date);
            Date to = sdf.parse(o.date);
            return from.compareTo(to);
        } catch (ParseException e) {
            return 0;
        }
    }
}
