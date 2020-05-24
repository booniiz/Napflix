public final class databaseAuthentication {
    /**
     * Replace address with your own address
     * Replace username with your own username
     * Replace password with your own password
     */
    private final String address = "jdbc:mysql://localhost:3306/Napflix";
    private final String username = "root";
    private final String passowrd = "5B2b43d5b3?";

    public String getAddress() {
        // Weird requirement by server....
        return address + "?verifyServerCertificate=false&useSSL=true";
    }

    public String getUsername() {
        return username;
    }

    public String getPassowrd() {
        return passowrd;
    }
}
