package org.smartregister.chw.model;

public class SbcMonthlySocialMediaReportModel {
    private String reportingMonth;

    private String organizationName;

    private String socialMediaHivMsgDistribution;

    private String numberBeneficiariesReachedFacebook;

    private String numberMessagesPublications;

    private String numberAiredMessagesBroadcasted;

    private String reportId;

    public String getReportingMonth() {
        return reportingMonth;
    }

    public void setReportingMonth(String reportingMonth) {
        this.reportingMonth = reportingMonth;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getSocialMediaHivMsgDistribution() {
        return socialMediaHivMsgDistribution;
    }

    public void setSocialMediaHivMsgDistribution(String socialMediaHivMsgDistribution) {
        this.socialMediaHivMsgDistribution = socialMediaHivMsgDistribution;
    }

    public String getNumberBeneficiariesReachedFacebook() {
        return numberBeneficiariesReachedFacebook;
    }

    public void setNumberBeneficiariesReachedFacebook(String numberBeneficiariesReachedFacebook) {
        this.numberBeneficiariesReachedFacebook = numberBeneficiariesReachedFacebook;
    }

    public String getNumberMessagesPublications() {
        return numberMessagesPublications;
    }

    public void setNumberMessagesPublications(String numberMessagesPublications) {
        this.numberMessagesPublications = numberMessagesPublications;
    }

    public String getNumberAiredMessagesBroadcasted() {
        return numberAiredMessagesBroadcasted;
    }

    public void setNumberAiredMessagesBroadcasted(String numberAiredMessagesBroadcasted) {
        this.numberAiredMessagesBroadcasted = numberAiredMessagesBroadcasted;
    }
}
