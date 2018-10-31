package fi.bizhop.kiekkohamsteri.dto;

public class RoundDto {
	private String tournament;
	private String link;
	private String date;
	private int round;
	private int score;
	private int rating;
	private int holes;
	private boolean included;
	private boolean doubled;

	public RoundDto(String tournament, String link, String date, int round, int score, int rating, int holes, boolean included) {
		this.tournament = tournament;
		this.link = link;
		this.date = date;
		this.round = round;
		this.score = score;
		this.rating = rating;
		this.holes = holes;
		this.included = included;
		this.doubled = false;
    }

    public RoundDto() {}

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getHoles() {
        return holes;
    }

    public void setHoles(int holes) {
        this.holes = holes;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public boolean isDoubled() {
        return doubled;
    }

    public void setDoubled(boolean doubled) {
        this.doubled = doubled;
    }
}
