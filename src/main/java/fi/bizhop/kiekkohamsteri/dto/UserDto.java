package fi.bizhop.kiekkohamsteri.dto;

public class UserDto {
	private Long id;
	private String email;
	private Integer level;
	
	public UserDto() {}
	
	public UserDto(Long id, String email, Integer level) {
		this.email = email;
		this.id = id;
		this.level = level;
	}

	public String getEmail() {
		return email;
	}
	public UserDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public Long getId() {
		return id;
	}

	public UserDto setId(Long id) {
		this.id = id;
		return this;
	}

	public Integer getLevel() {
		return level;
	}

	public UserDto setLevel(Integer level) {
		this.level = level;
		return this;
	}
}
