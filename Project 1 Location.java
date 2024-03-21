record Location(long id, int speedAmount, double latitude, double longitude, boolean speedBoostUsed) {
    public long getID() {
        return id;
    }

    @Override
    public String toString() {
        return "Location [ID: " + id + ", latitude: " + latitude + ", longitude: " + longitude + "]";
    }

    public int getSpeedAmount() {
        return speedAmount;
    }

    public void setUsedSpeedBoost(boolean usedSpeedBoost) {
    }
    public boolean usedSpeedBoost(){
        return speedBoostUsed;
    }
}
