package com.example.baoNgoCv.dto;

public class CompanyInformationUpdateDTO  {
    private String avatar;
    private Integer companySize;
    private String description;
    private Long industryId; // Đây sẽ là chuỗi, không phải đối tượng Industry

    public CompanyInformationUpdateDTO(String avatar, Integer companySize, String description, Long industryId) {
        this.avatar = avatar;
        this.companySize = companySize;
        this.description = description;
        this.industryId = industryId;
    }

    public CompanyInformationUpdateDTO() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getCompanySize() {
        return companySize;
    }

    public void setCompanySize(Integer companySize) {
        this.companySize = companySize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }
}
