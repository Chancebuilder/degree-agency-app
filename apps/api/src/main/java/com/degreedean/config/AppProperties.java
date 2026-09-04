package com.degreedean.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Seed seed = new Seed();
    private String disclosureVersion = "2026-09-01";
    private Simulator simulator = new Simulator();
    private Staleness staleness = new Staleness();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }
    public Seed getSeed() { return seed; }
    public void setSeed(Seed seed) { this.seed = seed; }
    public String getDisclosureVersion() { return disclosureVersion; }
    public void setDisclosureVersion(String disclosureVersion) { this.disclosureVersion = disclosureVersion; }
    public Simulator getSimulator() { return simulator; }
    public void setSimulator(Simulator simulator) { this.simulator = simulator; }
    public Staleness getStaleness() { return staleness; }
    public void setStaleness(Staleness staleness) { this.staleness = staleness; }

    public static class Jwt {
        private String secret;
        private int accessMinutes = 15;
        private int refreshDays = 14;

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public int getAccessMinutes() { return accessMinutes; }
        public void setAccessMinutes(int accessMinutes) { this.accessMinutes = accessMinutes; }
        public int getRefreshDays() { return refreshDays; }
        public void setRefreshDays(int refreshDays) { this.refreshDays = refreshDays; }
    }

    public static class Cors {
        private String origins = "";

        public String getOrigins() { return origins; }
        public void setOrigins(String origins) { this.origins = origins; }
    }

    public static class Seed {
        private String path = "../../data/seed";
        private boolean onStartup = true;

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public boolean isOnStartup() { return onStartup; }
        public void setOnStartup(boolean onStartup) { this.onStartup = onStartup; }
    }

    public static class Simulator {
        private HoursPerCredit hoursPerCredit = new HoursPerCredit();
        private Attrition attrition = new Attrition();
        private BestFitWeights bestFitWeights = new BestFitWeights();
        private double weeksPerSubscriptionMonth = 4.33;
        private double maxStaleAppliedRatio = 0.20;

        public HoursPerCredit getHoursPerCredit() { return hoursPerCredit; }
        public void setHoursPerCredit(HoursPerCredit hoursPerCredit) { this.hoursPerCredit = hoursPerCredit; }
        public Attrition getAttrition() { return attrition; }
        public void setAttrition(Attrition attrition) { this.attrition = attrition; }
        public BestFitWeights getBestFitWeights() { return bestFitWeights; }
        public void setBestFitWeights(BestFitWeights bestFitWeights) { this.bestFitWeights = bestFitWeights; }
        public double getWeeksPerSubscriptionMonth() { return weeksPerSubscriptionMonth; }
        public void setWeeksPerSubscriptionMonth(double weeksPerSubscriptionMonth) {
            this.weeksPerSubscriptionMonth = weeksPerSubscriptionMonth;
        }
        public double getMaxStaleAppliedRatio() { return maxStaleAppliedRatio; }
        public void setMaxStaleAppliedRatio(double maxStaleAppliedRatio) {
            this.maxStaleAppliedRatio = maxStaleAppliedRatio;
        }
    }

    public static class HoursPerCredit {
        private double sophiaTier = 5;
        private double studyStraighterTier = 9;
        private double exam = 12;
        private double competencyBased = 25;
        private double termBased = 40;
        private double pla = 7;
        private double plaOverhead = 10;

        public double getSophiaTier() { return sophiaTier; }
        public void setSophiaTier(double sophiaTier) { this.sophiaTier = sophiaTier; }
        public double getStudyStraighterTier() { return studyStraighterTier; }
        public void setStudyStraighterTier(double studyStraighterTier) { this.studyStraighterTier = studyStraighterTier; }
        public double getExam() { return exam; }
        public void setExam(double exam) { this.exam = exam; }
        public double getCompetencyBased() { return competencyBased; }
        public void setCompetencyBased(double competencyBased) { this.competencyBased = competencyBased; }
        public double getTermBased() { return termBased; }
        public void setTermBased(double termBased) { this.termBased = termBased; }
        public double getPla() { return pla; }
        public void setPla(double pla) { this.pla = pla; }
        public double getPlaOverhead() { return plaOverhead; }
        public void setPlaOverhead(double plaOverhead) { this.plaOverhead = plaOverhead; }
    }

    public static class Attrition {
        private double conservative = 0.25;
        private double standard = 0.15;
        private double aggressive = 0.05;

        public double getConservative() { return conservative; }
        public void setConservative(double conservative) { this.conservative = conservative; }
        public double getStandard() { return standard; }
        public void setStandard(double standard) { this.standard = standard; }
        public double getAggressive() { return aggressive; }
        public void setAggressive(double aggressive) { this.aggressive = aggressive; }
    }

    public static class BestFitWeights {
        private double cost = 0.3;
        private double time = 0.3;
        private double confidence = 0.4;

        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }
        public double getTime() { return time; }
        public void setTime(double time) { this.time = time; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }

    public static class Staleness {
        private int providerPricingDays = 30;
        private int tuitionDays = 90;
        private int policyDays = 180;
        private int requirementsDays = 180;
        private int equivalencyDays = 180;

        public int getProviderPricingDays() { return providerPricingDays; }
        public void setProviderPricingDays(int providerPricingDays) { this.providerPricingDays = providerPricingDays; }
        public int getTuitionDays() { return tuitionDays; }
        public void setTuitionDays(int tuitionDays) { this.tuitionDays = tuitionDays; }
        public int getPolicyDays() { return policyDays; }
        public void setPolicyDays(int policyDays) { this.policyDays = policyDays; }
        public int getRequirementsDays() { return requirementsDays; }
        public void setRequirementsDays(int requirementsDays) { this.requirementsDays = requirementsDays; }
        public int getEquivalencyDays() { return equivalencyDays; }
        public void setEquivalencyDays(int equivalencyDays) { this.equivalencyDays = equivalencyDays; }
    }
}
