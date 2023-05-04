package at.ac.fhcampuswien.se_booking.kafka;

public enum BookingAction {
    CREATE("create"), UPDATE("update"), DELETE("delete");
    private final String action;
    BookingAction(String action) {
        this.action = action;
    }

    @Override
    public String toString(){
        return action;
    }
}
