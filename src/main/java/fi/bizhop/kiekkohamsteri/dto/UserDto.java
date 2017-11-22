package fi.bizhop.kiekkohamsteri.dto;

public class UserDto {
	private Long id;
	private String email;
	
	public UserDto() {}
	
	public UserDto(Long id, String email) {
		this.email = email;
		this.id = id;
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
}
