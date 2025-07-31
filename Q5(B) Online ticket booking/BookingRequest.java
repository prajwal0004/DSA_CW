public class BookingRequest {
    public int seatRow, seatCol;
    public String user;

    public BookingRequest(String user, int row, int col) {
        this.user = user;
        this.seatRow = row;
        this.seatCol = col;
    }
}