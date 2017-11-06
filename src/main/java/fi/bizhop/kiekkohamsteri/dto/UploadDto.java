package fi.bizhop.kiekkohamsteri.dto;

public class UploadDto {
	private String data;
	private String name;
	
	public String getData() {
		return data;
	}
	public UploadDto setData(String data) {
		this.data = data;
		return this;
	}
	public String getName() {
		return name;
	}
	public UploadDto setName(String name) {
		this.name = name;
		return this;
	}
}
