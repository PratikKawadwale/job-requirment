package com.recruitment.web;

import com.recruitment.model.Application;
import com.recruitment.model.Candidate;
import com.recruitment.model.Company;
import com.recruitment.model.EligibilityResult;
import com.recruitment.model.Interview;
import com.recruitment.model.Job;
import com.recruitment.model.Offer;
import com.recruitment.model.Placement;

import java.util.Collection;
import java.util.Map;

/**
 * Lightweight JSON utility for serialization without external dependencies.
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String toJson(Candidate c) {
        if (c == null) return "null";
        return "{" +
                "\"candidateId\":" + c.getCandidateId() + "," +
                "\"candidateCode\":\"" + escape(c.getCandidateCode()) + "\"," +
                "\"fullName\":\"" + escape(c.getFullName()) + "\"," +
                "\"email\":\"" + escape(c.getEmail()) + "\"," +
                "\"mobile\":\"" + escape(c.getMobile()) + "\"," +
                "\"gender\":\"" + escape(c.getGender()) + "\"," +
                "\"qualification\":\"" + escape(c.getQualification()) + "\"," +
                "\"college\":\"" + escape(c.getCollege()) + "\"," +
                "\"university\":\"" + escape(c.getUniversity()) + "\"," +
                "\"passingYear\":" + c.getPassingYear() + "," +
                "\"percentage\":" + c.getDoublePercentage() + "," +
                "\"backlogCount\":" + c.getBacklogCount() + "," +
                "\"primaryTechnology\":\"" + escape(c.getPrimaryTechnology()) + "\"," +
                "\"currentLocation\":\"" + escape(c.getCurrentLocation()) + "\"," +
                "\"preferredLocation\":\"" + escape(c.getPreferredLocation()) + "\"," +
                "\"profileStatus\":\"" + escape(c.getProfileStatus()) + "\"," +
                "\"placementStatus\":\"" + escape(c.getPlacementStatus()) + "\"" +
                "}";
    }

    public static String toJson(Company c) {
        if (c == null) return "null";
        return "{" +
                "\"companyId\":" + c.getCompanyId() + "," +
                "\"companyCode\":\"" + escape(c.getCompanyCode()) + "\"," +
                "\"companyName\":\"" + escape(c.getCompanyName()) + "\"," +
                "\"industry\":\"" + escape(c.getIndustry()) + "\"," +
                "\"website\":\"" + escape(c.getWebsite()) + "\"," +
                "\"location\":\"" + escape(c.getLocation()) + "\"," +
                "\"companyType\":\"" + escape(c.getCompanyType()) + "\"," +
                "\"hrName\":\"" + escape(c.getHrName()) + "\"," +
                "\"hrEmail\":\"" + escape(c.getHrEmail()) + "\"," +
                "\"hrMobile\":\"" + escape(c.getHrMobile()) + "\"," +
                "\"status\":\"" + escape(c.getStatus()) + "\"" +
                "}";
    }

    public static String toJson(Job j) {
        if (j == null) return "null";
        return "{" +
                "\"jobId\":" + j.getJobId() + "," +
                "\"jobCode\":\"" + escape(j.getJobCode()) + "\"," +
                "\"companyId\":" + j.getCompanyId() + "," +
                "\"companyName\":\"" + escape(j.getCompanyName()) + "\"," +
                "\"jobTitle\":\"" + escape(j.getJobTitle()) + "\"," +
                "\"employmentType\":\"" + escape(j.getEmploymentType()) + "\"," +
                "\"location\":\"" + escape(j.getLocation()) + "\"," +
                "\"openings\":" + j.getOpenings() + "," +
                "\"minPackageLpa\":" + (j.getMinPackageLpa() != null ? j.getMinPackageLpa() : "0") + "," +
                "\"maxPackageLpa\":" + (j.getMaxPackageLpa() != null ? j.getMaxPackageLpa() : "0") + "," +
                "\"minPercentage\":" + j.getMinimumPercentage() + "," +
                "\"minPassingYear\":" + (j.getMinPassingYear() != null ? j.getMinPassingYear() : "0") + "," +
                "\"maxPassingYear\":" + (j.getMaxPassingYear() != null ? j.getMaxPassingYear() : "0") + "," +
                "\"maxBacklogs\":" + j.getMaxBacklogs() + "," +
                "\"qualificationRule\":\"" + escape(j.getQualificationRule()) + "\"," +
                "\"deadline\":\"" + (j.getDeadline() != null ? j.getDeadline().toString() : "") + "\"," +
                "\"status\":\"" + escape(j.getStatus()) + "\"" +
                "}";
    }

    public static String toJson(Application a) {
        if (a == null) return "null";
        return "{" +
                "\"applicationId\":" + a.getApplicationId() + "," +
                "\"applicationCode\":\"" + escape(a.getApplicationCode()) + "\"," +
                "\"candidateId\":" + a.getCandidateId() + "," +
                "\"candidateName\":\"" + escape(a.getCandidateName()) + "\"," +
                "\"jobId\":" + a.getJobId() + "," +
                "\"jobTitle\":\"" + escape(a.getJobTitle()) + "\"," +
                "\"companyName\":\"" + escape(a.getCompanyName()) + "\"," +
                "\"appliedDate\":\"" + (a.getAppliedDate() != null ? a.getAppliedDate().toString() : "") + "\"," +
                "\"eligibilityStatus\":\"" + escape(a.getEligibilityStatus()) + "\"," +
                "\"eligibilityReason\":\"" + escape(a.getEligibilityReason()) + "\"," +
                "\"applicationStatus\":\"" + escape(a.getApplicationStatus()) + "\"" +
                "}";
    }

    public static String toJson(Interview i) {
        if (i == null) return "null";
        return "{" +
                "\"interviewId\":" + i.getInterviewId() + "," +
                "\"applicationId\":" + i.getApplicationId() + "," +
                "\"candidateName\":\"" + escape(i.getCandidateName()) + "\"," +
                "\"companyName\":\"" + escape(i.getCompanyName()) + "\"," +
                "\"roundName\":\"" + escape(i.getRoundName()) + "\"," +
                "\"interviewDate\":\"" + (i.getInterviewDate() != null ? i.getInterviewDate().toString() : "") + "\"," +
                "\"interviewTime\":\"" + escape(i.getInterviewTime()) + "\"," +
                "\"interviewMode\":\"" + escape(i.getInterviewMode()) + "\"," +
                "\"interviewerName\":\"" + escape(i.getInterviewerName()) + "\"," +
                "\"status\":\"" + escape(i.getInterviewStatus()) + "\"," +
                "\"result\":\"" + escape(i.getResult()) + "\"" +
                "}";
    }

    public static String toJson(Offer o) {
        if (o == null) return "null";
        return "{" +
                "\"offerId\":" + o.getOfferId() + "," +
                "\"offerCode\":\"" + escape(o.getOfferCode()) + "\"," +
                "\"applicationId\":" + o.getApplicationId() + "," +
                "\"candidateName\":\"" + escape(o.getCandidateName()) + "\"," +
                "\"companyName\":\"" + escape(o.getCompanyName()) + "\"," +
                "\"designation\":\"" + escape(o.getDesignation()) + "\"," +
                "\"offeredPackage\":" + o.getOfferedPackage() + "," +
                "\"offerDate\":\"" + (o.getOfferDate() != null ? o.getOfferDate().toString() : "") + "\"," +
                "\"expectedJoiningDate\":\"" + (o.getJoiningDate() != null ? o.getJoiningDate().toString() : "") + "\"," +
                "\"offerStatus\":\"" + escape(o.getOfferStatus()) + "\"" +
                "}";
    }

    public static String toJson(Placement p) {
        if (p == null) return "null";
        return "{" +
                "\"placementId\":" + p.getPlacementId() + "," +
                "\"candidateId\":" + p.getCandidateId() + "," +
                "\"candidateName\":\"" + escape(p.getCandidateName()) + "\"," +
                "\"companyId\":" + p.getCompanyId() + "," +
                "\"companyName\":\"" + escape(p.getCompanyName()) + "\"," +
                "\"designation\":\"" + escape(p.getDesignation()) + "\"," +
                "\"finalPackage\":" + p.getFinalPackage() + "," +
                "\"actualJoiningDate\":\"" + (p.getActualJoiningDate() != null ? p.getActualJoiningDate().toString() : "") + "\"," +
                "\"joiningStatus\":\"" + escape(p.getJoiningStatus()) + "\"," +
                "\"placementStatus\":\"" + escape(p.getPlacementStatus()) + "\"" +
                "}";
    }

    public static String toJson(EligibilityResult r) {
        if (r == null) return "null";
        return "{" +
                "\"eligible\":" + r.isEligible() + "," +
                "\"status\":\"" + escape(r.getStatus()) + "\"," +
                "\"candidateId\":" + r.getCandidateId() + "," +
                "\"jobId\":" + r.getJobId() + "," +
                "\"reasons\":[" + String.join(",", r.getReasons().stream().map(s -> "\"" + escape(s) + "\"").toList()) + "]" +
                "}";
    }

    public static String mapToJson(Map<String, ? extends Number> map) {
        if (map == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ? extends Number> e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(e.getKey())).append("\":").append(e.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public static <T> String listToJson(Collection<T> list, java.util.function.Function<T, String> mapper) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (T item : list) {
            if (!first) sb.append(",");
            sb.append(mapper.apply(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
